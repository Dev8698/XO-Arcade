// Toast helper for displaying micro-notifications
function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type === 'info' ? 'primary' : type} border-0 show mb-2`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;
    container.appendChild(toast);
    
    // Auto-remove after 4 seconds
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 500);
    }, 4000);
}

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
let stompClient = null;

function connectLobbySocket() {
    if (!gameSessionId) return;

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = null; // Disable debug output

    stompClient.connect({}, function (frame) {
        // Subscribe to game session updates
        stompClient.subscribe(`/topic/game/${gameSessionId}`, function (messageOutput) {
            const session = JSON.parse(messageOutput.body);
            
            // If the game status transitions to PLAYING, redirect both users to board
            if (session.status === 'PLAYING') {
                document.getElementById('opponentStatus').innerHTML = `
                    <span class="text-neon-cyan"><i class="bi bi-person-check-fill me-2"></i>OPPONENT MATERIALIZED! LAUNCHING ARENA...</span>
                `;
                
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
document.addEventListener('DOMContentLoaded', () => {
    connectLobbySocket();
});
