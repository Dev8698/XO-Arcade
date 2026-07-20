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
            loadPendingRequests();
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
            loadFriendsPageData();
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
            loadFriendsPageData();
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
            loadFriendsPageData();
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

// FETCH AND RENDER PENDING REQUESTS
async function loadPendingRequests() {
    try {
        const response = await fetch('/api/friends/pending');
        if (!response.ok) return;
        const pending = await response.json();
        const container = document.getElementById('pending-requests-list');
        if (!container) return;

        if (pending.length === 0) {
            container.innerHTML = `
                <div class="text-center text-muted-custom py-4 small">
                    <i class="bi bi-check-circle fs-3 mb-2 d-block"></i> No pending invitations.
                </div>
            `;
            return;
        }

        container.innerHTML = '';
        pending.forEach(req => {
            const item = document.createElement('div');
            item.className = 'list-group-item bg-transparent border-secondary text-light d-flex align-items-center justify-content-between py-3 px-3';
            item.innerHTML = `
                <div class="d-flex align-items-center">
                    <img src="${req.sender.avatar || 'https://robohash.org/' + req.sender.username + '.png?set=set4'}" class="rounded-circle me-2" style="width:35px; height:35px;">
                    <span class="fw-semibold small">${req.sender.username}</span>
                </div>
                <div class="d-flex gap-1">
                    <button class="btn btn-sm btn-neon-cyan px-2 py-1 fs-8" onclick="acceptFriendRequestDirect(${req.id})">ACCEPT</button>
                    <button class="btn btn-sm btn-outline-secondary px-2 py-1 fs-8 text-muted-custom" onclick="rejectFriendRequestDirect(${req.id})">DECLINE</button>
                </div>
            `;
            container.appendChild(item);
        });
    } catch (err) {
        console.error("Error loading pending requests:", err);
    }
}

// FETCH AND RENDER FRIENDS LIST
async function loadFriendsList() {
    loadPendingRequests();
    try {
        const response = await fetch('/api/friends/active');
        if (!response.ok) return;
        const friends = await response.json();
        const container = document.getElementById('friends-list-container');
        const headerTitle = document.getElementById('friends-count-title');
        if (!container) return;

        if (headerTitle) {
            headerTitle.innerText = `MY FRIENDS (${friends.length})`;
        }

        if (friends.length === 0) {
            container.innerHTML = `
                <div class="text-center text-muted-custom py-5">
                    <i class="bi bi-person-x fs-1 mb-2 d-block"></i>
                    <h5>No Friends Yet</h5>
                    <p class="small">Use the search box on the left to add players to your friend circle.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = '';
        friends.forEach(friend => {
            const item = document.createElement('div');
            item.className = 'list-group-item bg-transparent border-secondary text-light d-flex align-items-center justify-content-between py-3 px-3';
            
            const btnChallenge = friend.online 
                ? `<button class="btn btn-sm btn-neon-cyan py-1 px-3 fs-8" onclick="challengeFromFriendsPage('${friend.id}')"><i class="bi bi-sword me-1"></i> CHALLENGE</button>`
                : `<button class="btn btn-sm btn-outline-secondary py-1 px-3 fs-8 text-muted-custom" disabled>CHALLENGE</button>`;

            item.innerHTML = `
                <div class="d-flex align-items-center">
                    <div class="position-relative">
                        <img src="${friend.avatar || 'https://robohash.org/' + friend.username + '.png?set=set4'}" class="rounded-circle me-2" style="width:45px; height:45px; border: 1px solid rgba(138,43,226,0.3)">
                        <span class="position-absolute bottom-0 end-0 p-1 rounded-circle border border-dark ${friend.online ? 'bg-success' : 'bg-secondary'}" style="width:10px; height:10px;"></span>
                    </div>
                    <div>
                        <span class="fw-semibold d-block text-white">${friend.username}</span>
                        <span class="text-muted-custom fs-8">${friend.online ? 'ONLINE' : 'OFFLINE'}</span>
                    </div>
                </div>
                
                <div class="d-flex gap-2">
                    ${btnChallenge}
                    <button class="btn btn-sm btn-outline-danger py-1 px-2 fs-8" onclick="removeFriend('${friend.id}')">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            `;
            container.appendChild(item);
        });
    } catch (err) {
        console.error("Error loading friends list:", err);
    }
}

function loadFriendsPageData() {
    loadPendingRequests();
    loadFriendsList();
}

// Load initialization
document.addEventListener('userReady', () => {
    loadFriendsPageData();
    
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
