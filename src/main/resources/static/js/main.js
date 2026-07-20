// Global State
let currentUser = null;
let stompClient = null;

// Toast helper for displaying micro-notifications
function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type === 'info' ? 'primary' : type === 'success' ? 'success' : 'danger'} border-0 show mb-2`;
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

// Global Challenge state
let currentChallengeId = null;
let challengeModal = null;

// Initialize WebSockets for real-time notifications
function connectGlobalSocket() {
    if (!currentUser) return;
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = null; // Disable debug output

    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/user/queue/notifications', function (notificationMsg) {
            const notification = JSON.parse(notificationMsg.body);
            handleGlobalNotification(notification);
        });
    }, function (error) {
        console.warn("WebSocket connection lost. Retrying in 5 seconds...", error);
        setTimeout(connectGlobalSocket, 5000);
    });
}

// Handle realtime WebSocket notifications
function handleGlobalNotification(notification) {
    if (notification.type === 'GAME_START') {
        window.location.href = `/game/board/${notification.gameSessionId}`;
        return;
    }

    // Show toast for notification
    showToast(notification.message, 'info');
    
    // Refresh local lists if they exist on the page
    if (typeof loadNotifications === 'function') loadNotifications();
    if (typeof loadFriendsList === 'function') loadFriendsList();
    
    // Refresh the notification badge
    updateNotificationBadge();

    // Trigger challenge invitation modal
    if (notification.type === 'GAME_INVITATION' && !notification.readStatus) {
        currentChallengeId = notification.id;
        
        const usernameEl = document.getElementById('challenger-username');
        const avatarEl = document.getElementById('challenger-avatar');
        
        if (usernameEl) usernameEl.innerText = notification.sender.username;
        if (avatarEl) avatarEl.src = notification.sender.avatar || 'https://robohash.org/guest.png?set=set4';
        
        const modalEl = document.getElementById('challengeReceivedModal');
        if (modalEl) {
            challengeModal = new bootstrap.Modal(modalEl);
            challengeModal.show();
        }
    }
}

// Accept/Decline challenges
async function acceptGameChallengeDirectly(notificationId) {
    try {
        const response = await fetch(`/api/game/challenge/${notificationId}/accept`, { method: 'POST' });
        if (response.ok) {
            const data = await response.json();
            window.location.href = `/game/board/${data.gameId}`;
        } else {
            showToast("Game session is no longer active.", "danger");
            updateNotificationBadge();
        }
    } catch (err) {
        console.error(err);
    }
}

async function rejectGameChallengeDirectly(notificationId) {
    try {
        await fetch(`/api/game/challenge/${notificationId}/reject`, { method: 'POST' });
        updateNotificationBadge();
    } catch (err) {
        console.error(err);
    }
}

async function acceptGameChallenge() {
    if (currentChallengeId) {
        await acceptGameChallengeDirectly(currentChallengeId);
        if (challengeModal) challengeModal.hide();
    }
}

async function rejectGameChallenge() {
    if (currentChallengeId) {
        await rejectGameChallengeDirectly(currentChallengeId);
        if (challengeModal) challengeModal.hide();
    }
}

// Fetch and render notification badge count
async function updateNotificationBadge() {
    try {
        const response = await fetch('/api/notifications');
        if (!response.ok) return;
        const notifications = await response.json();
        const badge = document.getElementById('notification-badge');
        if (!badge) return;
        
        const unreadCount = notifications.filter(n => !n.readStatus).length;
        if (unreadCount > 0) {
            badge.innerText = unreadCount;
            badge.classList.remove('d-none');
        } else {
            badge.classList.add('d-none');
        }
    } catch (error) {
        console.error("Error loading notification badge:", error);
    }
}

// Inject layouts dynamically into page placeholders
function injectCommonLayout() {
    const path = window.location.pathname;
    let activePage = 'dashboard';
    if (path.includes('/friends')) activePage = 'friends';
    if (path.includes('/history')) activePage = 'history';
    if (path.includes('/notifications')) activePage = 'notifications';
    if (path.includes('/profile')) activePage = 'profile';

    // Inject Navbar
    const navPlaceholder = document.getElementById('navbar-placeholder');
    if (navPlaceholder) {
        navPlaceholder.innerHTML = `
            <nav class="navbar navbar-expand-lg navbar-dark bg-dark-glass sticky-top">
                <div class="container">
                    <a class="navbar-brand d-flex align-items-center" href="/dashboard">
                        <span class="logo-icon me-2">🎮</span>
                        <span class="logo-text">XO <span class="text-neon-cyan">ARCADE</span></span>
                    </a>
                    
                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    
                    <div class="collapse navbar-collapse" id="navbarNav">
                        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                            <li class="nav-item">
                                <a class="nav-link ${activePage === 'dashboard' ? 'active' : ''}" href="/dashboard">
                                    <i class="bi bi-grid-1x2-fill me-1"></i> Dashboard
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${activePage === 'friends' ? 'active' : ''}" href="/friends">
                                    <i class="bi bi-people-fill me-1"></i> Friends
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${activePage === 'history' ? 'active' : ''}" href="/history">
                                    <i class="bi bi-clock-history me-1"></i> Match History
                                </a>
                            </li>
                        </ul>
                        
                        <div class="d-flex align-items-center gap-3">
                            <div class="position-relative">
                                <a href="/notifications" class="text-light nav-icon-btn position-relative ${activePage === 'notifications' ? 'active-icon' : ''}">
                                    <i class="bi bi-bell-fill fs-5"></i>
                                    <span id="notification-badge" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger d-none">0</span>
                                </a>
                            </div>

                            <div class="dropdown">
                                <a class="d-flex align-items-center text-decoration-none dropdown-toggle text-light cursor-pointer" href="#" role="button" id="userDropdown" data-bs-toggle="dropdown">
                                    <img id="nav-avatar" src="https://robohash.org/guest.png?set=set4" alt="Avatar" class="rounded-circle nav-avatar me-2" style="width: 35px; height: 35px; object-fit: cover; border: 2px solid var(--border-cyan);">
                                    <span class="d-none d-md-inline fw-semibold text-neon-pink" id="nav-username">Player</span>
                                </a>
                                <ul class="dropdown-menu dropdown-menu-end dropdown-menu-dark bg-dark-card border-neon-cyan">
                                    <li><a class="dropdown-item py-2" href="/profile"><i class="bi bi-person-circle me-2"></i> View Profile</a></li>
                                    <li><hr class="dropdown-divider border-secondary"></li>
                                    <li><button class="dropdown-item py-2 text-danger" onclick="logoutUser()"><i class="bi bi-box-arrow-right me-2"></i> Logout</button></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </nav>
        `;
    }

    // Inject Footer
    const footerPlaceholder = document.getElementById('footer-placeholder');
    if (footerPlaceholder) {
        footerPlaceholder.innerHTML = `
            <footer class="footer mt-auto py-3 bg-dark-glass text-center border-top border-dark-glow">
                <div class="container text-muted">
                    <span class="fs-6">&copy; 2026 XO Arcade. Built with Spring Boot, Supabase & WebSockets.</span>
                </div>
            </footer>
        `;
    }

    // Inject Challenge Modal
    const modalPlaceholder = document.getElementById('challenge-modal-placeholder');
    if (modalPlaceholder) {
        modalPlaceholder.innerHTML = `
            <div class="modal fade" id="challengeReceivedModal" data-bs-backdrop="static" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content bg-dark-card border-neon-pink text-light">
                        <div class="modal-header border-secondary py-3">
                            <h5 class="modal-title fw-bold text-neon-pink"><i class="bi bi-sword me-2"></i>CHALLENGE RECEIVED!</h5>
                        </div>
                        <div class="modal-body text-center py-4">
                            <div class="position-relative d-inline-block mb-3">
                                <img id="challenger-avatar" src="https://robohash.org/challenger.png?set=set4" alt="Challenger Avatar" class="rounded-circle border border-2 border-neon-pink" style="width: 80px; height: 80px; object-fit: cover;">
                            </div>
                            <h4 class="fw-bold text-white mb-2" id="challenger-username">GamerName</h4>
                            <p class="text-muted-custom mb-0">has challenged you to a Tic Tac Toe match!</p>
                        </div>
                        <div class="modal-footer border-secondary d-flex justify-content-between p-3">
                            <button type="button" class="btn btn-outline-secondary w-48 text-muted-custom" onclick="rejectGameChallenge()">DECLINE</button>
                            <button type="button" class="btn btn-neon-cyan w-48" onclick="acceptGameChallenge()">ACCEPT MATCH</button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }
}

// Authentication Check & Page Initialization
async function checkAuthAndInit() {
    const isAuthPage = window.location.pathname.endsWith('/login') || 
                       window.location.pathname.endsWith('/login.html') || 
                       window.location.pathname.endsWith('/signup') || 
                       window.location.pathname.endsWith('/signup.html');

    try {
        const response = await fetch('/api/users/me');
        if (!response.ok) {
            throw new Error("Unauthorized");
        }
        const data = await response.json();
        currentUser = data.user;

        // Redirect authenticated users away from login/signup to dashboard
        if (isAuthPage) {
            window.location.href = '/dashboard';
            return;
        }

        // Setup layouts & websockets
        injectCommonLayout();
        
        // Update user data on navbar
        const navUsername = document.getElementById('nav-username');
        const navAvatar = document.getElementById('nav-avatar');
        if (navUsername) navUsername.innerText = currentUser.username;
        if (navAvatar && currentUser.avatar) navAvatar.src = currentUser.avatar;

        updateNotificationBadge();
        connectGlobalSocket();

        // Let the page know user data is ready
        document.dispatchEvent(new CustomEvent('userReady', { detail: data }));

    } catch (err) {
        if (!isAuthPage) {
            window.location.href = '/login';
        }
    }
}

// Run layout initialization
document.addEventListener('DOMContentLoaded', () => {
    checkAuthAndInit();
});
