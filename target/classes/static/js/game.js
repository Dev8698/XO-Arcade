// Global Variables
let gameStompClient = null;
let isMyTurn = false;
let mySymbol = "";
let matchEndModalInstance = null;

// Connect to WebSocket and subscribe to session
function connectGameBoardSocket() {
    if (!sessionId) return;

    const socket = new SockJS('/ws');
    gameStompClient = Stomp.over(socket);
    gameStompClient.debug = null; // Mute console logging

    gameStompClient.connect({}, function (frame) {
        // Subscribe to game changes
        gameStompClient.subscribe(`/topic/game/${sessionId}`, function (messageOutput) {
            const session = JSON.parse(messageOutput.body);
            updateGameUI(session);
        });
    }, function (error) {
        console.warn("WebSocket disconnect detected in game. Retrying in 3 seconds...", error);
        setTimeout(connectGameBoardSocket, 3000);
    });
}

// Initialize Board
function initializeBoard() {
    // Determine player symbol
    if (currentUserId === playerXId) {
        mySymbol = "X";
    } else {
        mySymbol = "O";
    }
    
    connectGameBoardSocket();
    
    // Fallback draw of board from starting variables
    if (typeof initialBoardState !== 'undefined' && initialBoardState) {
        const mockSession = {
            boardState: initialBoardState,
            status: initialGameStatus,
            currentTurn: { id: initialCurrentTurnId },
            winner: { id: initialWinnerId }
        };
        // Ensure mock session variables are populated
        if (mockSession.currentTurn.id) {
            updateGameUI(mockSession);
        }
    }
}

// Update the Game UI based on the latest Session object
function updateGameUI(session) {
    // 1. Draw Board Cells
    const boardState = session.boardState;
    const cells = boardState.split(",");
    const cellElements = document.querySelectorAll('.game-cell');

    cellElements.forEach((cell, index) => {
        const val = cells[index];
        cell.innerText = val;
        cell.classList.remove('x-symbol', 'o-symbol');
        
        if (val === 'X') {
            cell.classList.add('x-symbol');
        } else if (val === 'O') {
            cell.classList.add('o-symbol');
        }
    });

    // 2. Set Turn States
    const indicator = document.getElementById('turnIndicator');
    
    if (session.status === 'PLAYING') {
        if (session.currentTurn.id === currentUserId) {
            isMyTurn = true;
            if (indicator) {
                indicator.innerText = "YOUR TURN";
                indicator.className = "badge bg-info text-dark px-3 py-2 fs-6 pulse-glow";
            }
        } else {
            isMyTurn = false;
            if (indicator) {
                indicator.innerText = "OPPONENT'S TURN";
                indicator.className = "badge bg-secondary text-light px-3 py-2 fs-6";
            }
        }
    }

    // 3. Check for Game Completion
    if (session.status === 'WON') {
        isMyTurn = false;
        if (indicator) {
            indicator.innerText = "MATCH COMPLETED";
            indicator.className = "badge bg-danger text-light px-3 py-2 fs-6";
        }

        const winnerId = session.winner.id;
        const title = document.getElementById('matchEndTitle');
        const msg = document.getElementById('matchEndMessage');
        const modalContainer = document.getElementById('modalBorderContainer');

        if (title && msg && modalContainer) {
            if (winnerId === currentUserId) {
                title.innerText = "VICTORY! 🎉";
                title.className = "fw-extrabold text-neon-cyan mb-3";
                msg.innerText = "You have dominated the grid. Rank score increased!";
                modalContainer.className = "modal-content bg-dark-card text-light border-neon-cyan shadow-lg";
            } else {
                title.innerText = "DEFEAT... 💀";
                title.className = "fw-extrabold text-neon-pink mb-3";
                msg.innerText = "Your opponent gained control of the grid. Try again!";
                modalContainer.className = "modal-content bg-dark-card text-light border-neon-pink shadow-lg";
            }
        }

        showMatchEndModal();
    } else if (session.status === 'DRAW') {
        isMyTurn = false;
        if (indicator) {
            indicator.innerText = "DRAW GAME";
            indicator.className = "badge bg-warning text-dark px-3 py-2 fs-6";
        }

        const title = document.getElementById('matchEndTitle');
        const msg = document.getElementById('matchEndMessage');
        const modalContainer = document.getElementById('modalBorderContainer');

        if (title && msg && modalContainer) {
            title.innerText = "DRAW GAME 🤝";
            title.className = "fw-extrabold text-purple mb-3";
            title.style.color = "#b388ff";
            msg.innerText = "Grid lock occurred. Neither side broke through.";
            modalContainer.className = "modal-content bg-dark-card text-light border-purple shadow-lg";
        }

        showMatchEndModal();
    }
}

// Trigger move event
function makeCellMove(cellIndex) {
    if (!isMyTurn) {
        showToast("It is not your turn!", "warning");
        return;
    }

    // Send move to server over stomp
    if (gameStompClient && gameStompClient.connected) {
        const payload = {
            gameId: sessionId,
            playerId: currentUserId,
            cellIndex: cellIndex
        };
        gameStompClient.send("/app/game/move", {}, JSON.stringify(payload));
    } else {
        showToast("WebSocket disconnected. Attempting reconnection...", "danger");
    }
}

// Show End Modal
function showMatchEndModal() {
    if (!matchEndModalInstance) {
        const modalEl = document.getElementById('matchEndModal');
        if (modalEl) {
            matchEndModalInstance = new bootstrap.Modal(modalEl);
        }
    }
    if (matchEndModalInstance) {
        matchEndModalInstance.show();
    }
}

// Rematch redirect
function requestRematch() {
    if (matchEndModalInstance) {
        matchEndModalInstance.hide();
    }
    // Redirecting to create a new session lobby is the fastest route
    window.location.href = '/game/create';
}

// Forfeit game
async function leaveGameMatch() {
    if (!confirm("Are you sure you want to forfeit? This counts as an immediate defeat.")) {
        return;
    }
    try {
        const response = await fetch(`/api/game/leave/${sessionId}`, { method: 'POST' });
        if (response.ok) {
            window.location.href = '/dashboard';
        }
    } catch (e) {
        console.error("Forfeit error:", e);
    }
}
