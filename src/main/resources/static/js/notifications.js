// Mark single notification read
async function markSingleReadFromPage(id) {
    try {
        const response = await fetch(`/api/notifications/${id}/read`, { method: 'POST' });
        if (response.ok) {
            loadNotificationsPageData();
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
            loadNotificationsPageData();
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
            loadNotificationsPageData();
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
            loadNotificationsPageData();
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
            loadNotificationsPageData();
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
            loadNotificationsPageData();
        }
    } catch (err) {
        console.error(err);
    }
}

// Fetch and Render notifications specifically for the notifications view
async function loadNotificationsPageData() {
    try {
        const response = await fetch('/api/notifications');
        if (!response.ok) return;
        const notifications = await response.json();
        
        const container = document.getElementById('notifications-page-list');
        const headerBtn = document.getElementById('mark-all-read-header-btn');
        if (!container) return;

        // Toggle 'Mark all read' button
        if (notifications.length > 0) {
            if (headerBtn) headerBtn.classList.remove('d-none');
        } else {
            if (headerBtn) headerBtn.classList.add('d-none');
        }

        if (notifications.length === 0) {
            container.innerHTML = `
                <div class="text-center text-muted-custom py-5">
                    <i class="bi bi-bell-slash fs-1 text-secondary mb-3 d-block"></i>
                    <h5>Your Grid is Clear</h5>
                    <p class="small mb-0">No alerts or messages to display. Play a game or add a friend to start receiving updates.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = '';
        notifications.forEach(notif => {
            const item = document.createElement('div');
            const unreadClass = notif.readStatus ? '' : 'bg-dark-glass border-start border-3 border-neon-cyan';
            item.className = `list-group-item bg-transparent border-secondary text-light py-4 px-4 d-flex align-items-start justify-content-between ${unreadClass}`;
            
            // Format Time
            const dateObj = new Date(notif.createdTime);
            const dateStr = dateObj.toLocaleDateString();
            const timeStr = dateObj.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

            // Emoji Icon type selector
            let emoji = '🔔';
            if (notif.type === 'FRIEND_REQUEST') emoji = '👤';
            else if (notif.type === 'FRIEND_ACCEPT') emoji = '🤝';
            else if (notif.type === 'GAME_INVITATION') emoji = '⚔️';
            else if (notif.type === 'MATCH_RESULT') emoji = '🏆';

            let actionButtons = '';
            if (!notif.readStatus) {
                if (notif.type === 'FRIEND_REQUEST') {
                    actionButtons = `
                        <div class="mt-2 d-flex gap-2">
                            <button class="btn btn-sm btn-neon-cyan px-3 py-1 fs-8" onclick="acceptRequestFromPage(${notif.id})">ACCEPT</button>
                            <button class="btn btn-sm btn-outline-secondary px-3 py-1 fs-8 text-muted-custom" onclick="rejectRequestFromPage(${notif.id})">REJECT</button>
                        </div>
                    `;
                } else if (notif.type === 'GAME_INVITATION') {
                    actionButtons = `
                        <div class="mt-2 d-flex gap-2">
                            <button class="btn btn-sm btn-neon-cyan px-3 py-1 fs-8" onclick="acceptChallengeFromPage(${notif.id})">ACCEPT MATCH</button>
                            <button class="btn btn-sm btn-outline-secondary px-3 py-1 fs-8 text-muted-custom" onclick="rejectChallengeFromPage(${notif.id})">DECLINE</button>
                        </div>
                    `;
                }
            }

            const checkMarkBtn = notif.readStatus ? '' : `
                <button class="btn btn-link text-neon-cyan p-0 text-decoration-none fs-8" onclick="markSingleReadFromPage(${notif.id})">
                    <i class="bi bi-check-lg me-1"></i> Mark read
                </button>
            `;

            item.innerHTML = `
                <div class="d-flex align-items-start gap-3">
                    <div class="fs-4">${emoji}</div>
                    <div>
                        <p class="m-0 text-white fw-semibold small">${notif.message}</p>
                        <span class="text-muted-custom fs-8">${dateStr} at ${timeStr}</span>
                        ${actionButtons}
                    </div>
                </div>
                ${checkMarkBtn}
            `;
            container.appendChild(item);
        });

        // Trigger updates on global notification badge if it exists
        if (typeof updateNotificationBadge === 'function') {
            updateNotificationBadge();
        }
    } catch (err) {
        console.error("Error loading notifications list:", err);
    }
}

function loadNotifications() {
    loadNotificationsPageData();
}

// Load initialization
document.addEventListener('userReady', () => {
    loadNotificationsPageData();
});
