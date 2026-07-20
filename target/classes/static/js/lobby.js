// Copy Game Code to Clipboard
function copyRoomCode() {
    const codeElement = document.getElementById('roomCode');
    if (!codeElement) return;
    
    const codeText = codeElement.innerText.trim();
    navigator.clipboard.writeText(codeText).then(() => {
        showToast("Game code copied to clipboard!", "success");
    }).catch(err => {
        console.error("Failed to copy text: ", err);
    });
}

// Abort Lobby / Cancel Game
async function cancelLobby(gameId) {
    try {
        const response = await fetch(`/api/game/cancel/${gameId}`, { method: 'POST' });
        if (response.ok) {
            window.location.href = '/dashboard';
        } else {
            showToast("Failed to abort lobby.", "danger");
        }
    } catch (err) {
        console.error("Lobby cancel error:", err);
    }
}

// WebSocket Connection to listen for matchmaking
let lobbyStompClient = null;

function connectLobbySocket() {
    if (!gameSessionId) return;

    const socket = new SockJS('/ws');
    lobbyStompClient = Stomp.over(socket);
    lobbyStompClient.debug = null; // Disable debug output

    lobbyStompClient.connect({}, function (frame) {
        // Subscribe to game session updates
        lobbyStompClient.subscribe(`/topic/game/${gameSessionId}`, function (messageOutput) {
            const session = JSON.parse(messageOutput.body);
            
            // If the game status transitions to PLAYING, redirect both users to board
            if (session.status === 'PLAYING') {
                const opponentStatus = document.getElementById('opponentStatus');
                if (opponentStatus) {
                    opponentStatus.innerHTML = `
                        <span class="text-neon-cyan"><i class="bi bi-person-check-fill me-2"></i>OPPONENT MATERIALIZED! LAUNCHING ARENA...</span>
                    `;
                }
                
                showToast("Opponent joined! Relocating to combat board...", "success");
                
                setTimeout(() => {
                    window.location.href = `/game/board/${session.id}`;
                }, 1500);
            }
        });
    }, function (error) {
        console.warn("WebSocket connection lost in lobby. Retrying in 5 seconds...", error);
        setTimeout(connectLobbySocket, 5000);
    });
}

// Page load initialization
document.addEventListener('userReady', () => {
    connectLobbySocket();
});
