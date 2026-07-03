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

// Mark single notification read
async function markSingleReadFromPage(id) {
    try {
        const response = await fetch(`/api/notifications/${id}/read`, { method: 'POST' });
        if (response.ok) {
            window.location.reload();
        }
    } catch (err) {
        console.error(err);
    }
}

// Mark all notifications read
async function markAllNotificationsReadPage() {
    try {
        const response = await fetch('/api/notifications/read-all', { method: 'POST' });
        if (response.ok) {
            window.location.reload();
        }
    } catch (err) {
        console.error(err);
    }
}

// Accept friend request from notification
async function acceptRequestFromPage(notificationId) {
    try {
        const response = await fetch(`/api/friends/request/${notificationId}/accept`, { method: 'POST' });
        if (response.ok) {
            showToast("Friend request accepted!", "success");
            setTimeout(() => window.location.reload(), 1000);
        }
    } catch (err) {
        console.error(err);
    }
}

// Reject friend request from notification
async function rejectRequestFromPage(notificationId) {
    try {
        const response = await fetch(`/api/friends/request/${notificationId}/reject`, { method: 'POST' });
        if (response.ok) {
            showToast("Friend request rejected.", "info");
            setTimeout(() => window.location.reload(), 1000);
        }
    } catch (err) {
        console.error(err);
    }
}

// Accept game challenge from notification
async function acceptChallengeFromPage(notificationId) {
    try {
        const response = await fetch(`/api/game/challenge/${notificationId}/accept`, { method: 'POST' });
        if (response.ok) {
            const data = await response.json();
            window.location.href = `/game/board/${data.gameId}`;
        } else {
            showToast("Game session is no longer active.", "danger");
            setTimeout(() => window.location.reload(), 1000);
        }
    } catch (err) {
        console.error(err);
    }
}

// Reject game challenge from notification
async function rejectChallengeFromPage(notificationId) {
    try {
        const response = await fetch(`/api/game/challenge/${notificationId}/reject`, { method: 'POST' });
        if (response.ok) {
            showToast("Game challenge declined.", "info");
            setTimeout(() => window.location.reload(), 1000);
        }
    } catch (err) {
        console.error(err);
    }
}

// Load initialization
document.addEventListener('DOMContentLoaded', () => {
    connectWebSocket();
});
