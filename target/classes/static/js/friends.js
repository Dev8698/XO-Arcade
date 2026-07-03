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

// Global state for current game challenge
let currentChallengeId = null;
let challengeModal = null;
let stompClient = null;

function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/user/queue/notifications', function (notificationMsg) {
            const notification = JSON.parse(notificationMsg.body);
            handleRealtimeNotification(notification);
        });
    }, function (error) {
        console.warn("WebSocket connection lost. Retrying in 5 seconds...", error);
        setTimeout(connectWebSocket, 5000);
    });
}

function handleRealtimeNotification(notification) {
    if (notification.type === 'GAME_START') {
        window.location.href = `/game/board/${notification.gameSessionId}`;
        return;
    }

    showToast(notification.message, 'info');
    
    // If it's a game invitation challenge, trigger modal
    if (notification.type === 'GAME_INVITATION' && !notification.readStatus) {
        currentChallengeId = notification.id;
        
        document.getElementById('challenger-username').innerText = notification.sender.username;
        document.getElementById('challenger-avatar').src = notification.sender.avatar || 'https://robohash.org/guest.png?set=set4';
        
        if (!challengeModal) {
            challengeModal = new bootstrap.Modal(document.getElementById('challengeReceivedModal'));
        }
        challengeModal.show();
    }
}

// Accept/Decline challenge from modal popup
async function acceptGameChallenge() {
    if (currentChallengeId) {
        try {
            const response = await fetch(`/api/game/challenge/${currentChallengeId}/accept`, { method: 'POST' });
            if (response.ok) {
                const data = await response.json();
                window.location.href = `/game/board/${data.gameId}`;
            } else {
                showToast("Game session is no longer active.", "danger");
            }
        } catch (err) {
            console.error(err);
        }
        challengeModal.hide();
    }
}

async function rejectGameChallenge() {
    if (currentChallengeId) {
        try {
            await fetch(`/api/game/challenge/${currentChallengeId}/reject`, { method: 'POST' });
        } catch (err) {
            console.error(err);
        }
        challengeModal.hide();
    }
}

// SEARCH USER PLAYERS
async function searchPlayers() {
    const input = document.getElementById('searchPlayerInput');
    const query = input.value.trim();
    const resultsContainer = document.getElementById('search-results-list');
    
    if (query.length < 2) {
        resultsContainer.innerHTML = '<div class="text-center text-muted-custom py-3 small">Please type at least 2 characters.</div>';
        return;
    }

    resultsContainer.innerHTML = `
        <div class="text-center py-4">
            <div class="spinner-border spinner-border-sm text-neon-cyan" role="status"></div>
            <span class="ms-2 text-muted-custom small">Scanning network...</span>
        </div>
    `;

    try {
        const response = await fetch(`/api/friends/search?query=${encodeURIComponent(query)}`);
        if (!response.ok) throw new Error("Search request failed.");
        const players = await response.json();

        if (players.length === 0) {
            resultsContainer.innerHTML = '<div class="text-center text-muted-custom py-3 small">No players found matching that name.</div>';
            return;
        }

        resultsContainer.innerHTML = '';
        players.forEach(player => {
            const item = document.createElement('div');
            item.className = 'list-group-item bg-transparent border-secondary text-light d-flex align-items-center justify-content-between py-2 px-1';
            item.innerHTML = `
                <div class="d-flex align-items-center">
                    <img src="${player.avatar || 'https://robohash.org/' + player.username + '.png?set=set4'}" alt="Avatar" class="rounded-circle me-2" style="width: 32px; height: 32px;">
                    <span class="small fw-semibold">${player.username}</span>
                </div>
                <button class="btn btn-sm btn-neon-cyan fs-9 py-1 px-2" id="search-btn-${player.id}" onclick="sendFriendRequest('${player.id}')">
                    <i class="bi bi-person-plus-fill"></i> ADD
                </button>
            `;
            resultsContainer.appendChild(item);
        });
    } catch (err) {
        console.error("Search error:", err);
        resultsContainer.innerHTML = '<div class="text-center text-danger py-3 small">Search failed.</div>';
    }
}

// SEND FRIEND REQUEST
async function sendFriendRequest(receiverId) {
    const btn = document.getElementById(`search-btn-${receiverId}`);
    if (btn) btn.disabled = true;

    try {
        const response = await fetch(`/api/friends/request/send/${receiverId}`, { method: 'POST' });
        if (response.ok) {
            showToast("Friend request sent!", "success");
            if (btn) {
                btn.className = 'btn btn-sm btn-outline-secondary fs-9 py-1 px-2 text-muted-custom';
                btn.innerHTML = '<i class="bi bi-check-lg"></i> SENT';
            }
        } else {
            const errorMsg = await response.text();
            showToast(errorMsg || "Unable to send request.", "danger");
            if (btn) btn.disabled = false;
        }
    } catch (err) {
        console.error(err);
        showToast("Server error occurred.", "danger");
        if (btn) btn.disabled = false;
    }
}

// ACCEPT FRIEND REQUEST DIRECTLY
async function acceptFriendRequestDirect(requestId) {
    try {
        const response = await fetch(`/api/friends/request/direct/${requestId}/accept`, { method: 'POST' });
        if (response.ok) {
            showToast("Friend request accepted!", "success");
            setTimeout(() => window.location.reload(), 1000);
        } else {
            showToast("Failed to accept request.", "danger");
        }
    } catch (err) {
        console.error(err);
    }
}

// REJECT FRIEND REQUEST DIRECTLY
async function rejectFriendRequestDirect(requestId) {
    try {
        const response = await fetch(`/api/friends/request/direct/${requestId}/reject`, { method: 'POST' });
        if (response.ok) {
            showToast("Request declined.", "info");
            setTimeout(() => window.location.reload(), 1000);
        } else {
            showToast("Failed to decline request.", "danger");
        }
    } catch (err) {
        console.error(err);
    }
}

// REMOVE FRIEND
async function removeFriend(friendId) {
    if (!confirm("Are you sure you want to remove this friend? This will delete mutual relationship data.")) {
        return;
    }

    try {
        const response = await fetch(`/api/friends/remove/${friendId}`, { method: 'DELETE' });
        if (response.ok) {
            showToast("Friend removed.", "info");
            setTimeout(() => window.location.reload(), 1000);
        } else {
            showToast("Unable to remove friend.", "danger");
        }
    } catch (err) {
        console.error(err);
    }
}

// CHALLENGE FRIEND
async function challengeFromFriendsPage(friendId) {
    try {
        const response = await fetch(`/api/friends/challenge/${friendId}`, { method: 'POST' });
        if (response.ok) {
            showToast("Challenge sent!", "success");
        } else {
            showToast("Unable to send challenge. Friend might be offline or in game.", "danger");
        }
    } catch (err) {
        console.error(err);
    }
}

// Load initialization
document.addEventListener('DOMContentLoaded', () => {
    connectWebSocket();
    
    // Add enter-key listener for search box
    const searchInput = document.getElementById('searchPlayerInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                searchPlayers();
            }
        });
    }
});
