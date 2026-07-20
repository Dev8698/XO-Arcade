// Fetch and render friends list
async function loadFriendsList() {
    try {
        const response = await fetch('/api/friends/active');
        if (!response.ok) return;
        const friends = await response.json();
        
        const listContainer = document.getElementById('dashboard-friends-list');
        const emptyPlaceholder = document.getElementById('friends-empty-placeholder');
        
        if (friends.length === 0) {
            listContainer.innerHTML = '';
            listContainer.appendChild(emptyPlaceholder);
            return;
        }

        listContainer.innerHTML = '';
        friends.forEach(friend => {
            const friendRow = document.createElement('div');
            friendRow.className = 'list-group-item bg-transparent border-secondary text-light d-flex align-items-center justify-content-between py-3 px-3';
            
            const onlineStatusDot = friend.online 
                ? '<span class="badge bg-success rounded-circle p-1 ms-2" style="width:8px; height:8px; display:inline-block;" title="Online"></span>' 
                : '<span class="badge bg-secondary rounded-circle p-1 ms-2" style="width:8px; height:8px; display:inline-block;" title="Offline"></span>';

            friendRow.innerHTML = `
                <div class="d-flex align-items-center">
                    <img src="${friend.avatar || 'https://robohash.org/' + friend.username + '.png?set=set4'}" alt="Avatar" class="rounded-circle me-2" style="width: 40px; height: 40px; border: 1px solid rgba(138,43,226,0.3)">
                    <div>
                        <span class="fw-semibold text-light">${friend.username}</span>
                        ${onlineStatusDot}
                    </div>
                </div>
                ${friend.online ? `
                    <button class="btn btn-sm btn-neon-cyan px-2 py-1 fs-8" onclick="challengeFriend('${friend.id}')">
                        <i class="bi bi-sword me-1"></i> CHALLENGE
                    </button>
                ` : `
                    <button class="btn btn-sm btn-outline-secondary px-2 py-1 fs-8 text-muted-custom" disabled>
                        OFFLINE
                    </button>
                `}
            `;
            listContainer.appendChild(friendRow);
        });
    } catch (error) {
        console.error("Error loading friends list:", error);
    }
}

// Fetch and render notifications
async function loadNotifications() {
    try {
        const response = await fetch('/api/notifications');
        if (!response.ok) return;
        const notifications = await response.json();
        
        const listContainer = document.getElementById('dashboard-notifications-list');
        const emptyPlaceholder = document.getElementById('notifications-empty-placeholder');
        const badge = document.getElementById('notification-badge');
        
        const unreadCount = notifications.filter(n => !n.readStatus).length;
        if (unreadCount > 0) {
            badge.innerText = unreadCount;
            badge.classList.remove('d-none');
        } else {
            badge.classList.add('d-none');
        }

        if (notifications.length === 0) {
            listContainer.innerHTML = '';
            listContainer.appendChild(emptyPlaceholder);
            return;
        }

        listContainer.innerHTML = '';
        
        // Show only latest 3 notifications on dashboard card
        const showMoreBtnNeeded = notifications.length > 3;
        const displayList = notifications.slice(0, 3);
        
        displayList.forEach(notif => {
            const item = document.createElement('div');
            const unreadClass = notif.readStatus ? '' : 'bg-dark-glass border-start border-neon-cyan';
            item.className = `list-group-item bg-transparent border-secondary text-light py-3 px-3 ${unreadClass}`;
            
            // Format time ago
            const timeString = new Date(notif.createdTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
            
            let actionButtons = '';
            if (notif.type === 'FRIEND_REQUEST' && !notif.readStatus) {
                actionButtons = `
                    <div class="mt-2 d-flex gap-2">
                        <button class="btn btn-sm btn-neon-cyan px-3 py-1 fs-8" onclick="acceptFriendRequest(${notif.id})">ACCEPT</button>
                        <button class="btn btn-sm btn-outline-secondary px-3 py-1 fs-8 text-muted-custom" onclick="rejectFriendRequest(${notif.id})">REJECT</button>
                    </div>
                `;
            } else if (notif.type === 'GAME_INVITATION' && !notif.readStatus) {
                actionButtons = `
                    <div class="mt-2 d-flex gap-2">
                        <button class="btn btn-sm btn-neon-cyan px-3 py-1 fs-8" onclick="acceptGameChallengeDirectly(${notif.id})">ACCEPT</button>
                        <button class="btn btn-sm btn-outline-secondary px-3 py-1 fs-8 text-muted-custom" onclick="rejectGameChallengeDirectly(${notif.id})">DECLINE</button>
                    </div>
                `;
            }

            item.innerHTML = `
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <p class="m-0 text-light small">${notif.message}</p>
                        <span class="text-muted-custom fs-8">${timeString}</span>
                    </div>
                    ${notif.readStatus ? '' : `
                        <button class="btn btn-link text-muted-custom p-0 text-decoration-none fs-8" onclick="markAsRead(${notif.id})">
                            <i class="bi bi-check-lg"></i> Mark read
                        </button>
                    `}
                </div>
                ${actionButtons}
            `;
            listContainer.appendChild(item);
        });

        if (showMoreBtnNeeded) {
            const showMoreRow = document.createElement('div');
            showMoreRow.className = 'list-group-item bg-transparent text-center border-secondary py-3 px-3';
            showMoreRow.innerHTML = `
                <a href="/notifications" class="btn btn-sm btn-neon-cyan px-4 py-2 w-100">
                    <i class="bi bi-arrow-right-circle-fill me-2"></i>SHOW MORE
                </a>
            `;
            listContainer.appendChild(showMoreRow);
        }
    } catch (error) {
        console.error("Error loading notifications:", error);
    }
}

// Challenge friend
async function challengeFriend(friendId) {
    try {
        const response = await fetch(`/api/friends/challenge/${friendId}`, { method: 'POST' });
        if (response.ok) {
            showToast("Challenge invitation sent!", "success");
        } else {
            showToast("Failed to send challenge. Opponent might be busy.", "danger");
        }
    } catch (err) {
        console.error("Challenge error:", err);
    }
}

// Mark single notification read
async function markAsRead(id) {
    try {
        await fetch(`/api/notifications/${id}/read`, { method: 'POST' });
        loadNotifications();
    } catch (err) {
        console.error(err);
    }
}

// Mark all notifications read
async function markAllNotificationsRead() {
    try {
        await fetch('/api/notifications/read-all', { method: 'POST' });
        loadNotifications();
    } catch (err) {
        console.error(err);
    }
}

// Accept/Decline Friend request inside notifications list
async function acceptFriendRequest(notificationId) {
    try {
        const response = await fetch(`/api/friends/request/${notificationId}/accept`, { method: 'POST' });
        if (response.ok) {
            showToast("Friend request accepted!", "success");
            loadNotifications();
            loadFriendsList();
        }
    } catch (err) {
        console.error(err);
    }
}

async function rejectFriendRequest(notificationId) {
    try {
        const response = await fetch(`/api/friends/request/${notificationId}/reject`, { method: 'POST' });
        if (response.ok) {
            showToast("Friend request rejected.", "info");
            loadNotifications();
        }
    } catch (err) {
        console.error(err);
    }
}

// Join game session via code input box
async function joinGameSession() {
    const codeInput = document.getElementById('gameCodeInput');
    const errDiv = document.getElementById('join-game-error');
    const code = codeInput.value.trim().toUpperCase();
    
    errDiv.innerText = '';
    codeInput.classList.remove('is-invalid');
    
    if (code.length !== 6) {
        codeInput.classList.add('is-invalid');
        errDiv.innerText = 'Game code must be exactly 6 characters.';
        return;
    }

    try {
        const response = await fetch(`/api/game/join?code=${code}`, { method: 'POST' });
        if (response.ok) {
            const gameSession = await response.json();
            // Redirect to board or waiting room based on session status
            if (gameSession.status === 'PLAYING') {
                window.location.href = `/game/board/${gameSession.id}`;
            } else {
                window.location.href = `/game/waiting/${gameSession.id}`;
            }
        } else {
            const errorMsg = await response.text();
            codeInput.classList.add('is-invalid');
            errDiv.innerText = errorMsg || 'Invalid or full game room.';
        }
    } catch (error) {
        console.error("Error joining game:", error);
        codeInput.classList.add('is-invalid');
        errDiv.innerText = 'Server error. Please try again.';
    }
}

// Page load initialization
document.addEventListener('userReady', () => {
    loadFriendsList();
    loadNotifications();
    
    // Poll for status updates occasionally as fallback
    setInterval(() => {
        loadFriendsList();
        loadNotifications();
    }, 15000);
});
