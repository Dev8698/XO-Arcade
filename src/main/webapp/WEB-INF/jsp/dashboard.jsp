<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="navbar.jsp" %>

<div class="row g-4">
    <!-- LEFT PANEL: USER CARD & FRIENDS OVERVIEW -->
    <div class="col-lg-4 col-md-5">
        <!-- GAMER PROFILE CARD -->
        <div class="card bg-dark-card border-neon-cyan mb-4">
            <div class="card-body text-center">
                <div class="position-relative d-inline-block mb-3">
                    <img src="${currentUser.avatar != null ? currentUser.avatar : 'https://robohash.org/guest.png?set=set4'}" 
                         alt="Gamer Avatar" 
                         class="rounded-circle border border-2 border-neon-cyan"
                         style="width: 100px; height: 100px; object-fit: cover;">
                    <span class="position-absolute bottom-0 end-0 p-2 bg-success border border-dark rounded-circle pulse-glow" title="Online"></span>
                </div>
                <h4 class="fw-bold text-neon-cyan mb-1">${currentUser.username}</h4>
                <p class="text-muted-custom small mb-3">${currentUser.email}</p>
                
                <!-- Quick Stats Grid -->
                <div class="row g-2 border-top border-dark-glow pt-3 mt-2">
                    <div class="col-4">
                        <div class="fw-bold text-white small">PLAYED</div>
                        <div class="fs-5 text-light fw-bold">${currentUser.gamesPlayed}</div>
                    </div>
                    <div class="col-4">
                        <div class="fw-bold text-neon-cyan small">WINS</div>
                        <div class="fs-5 text-neon-cyan fw-bold">${currentUser.wins}</div>
                    </div>
                    <div class="col-4">
                        <div class="fw-bold text-neon-pink small">WIN %</div>
                        <div class="fs-5 text-neon-pink fw-bold">${currentUser.winPercentage}%</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- QUICK FRIENDS LIST -->
        <div class="card bg-dark-card border-purple">
            <div class="card-header bg-transparent border-secondary d-flex justify-content-between align-items-center py-3">
                <h5 class="m-0 fw-bold text-light"><i class="bi bi-people-fill me-2 text-neon-pink"></i>FRIENDS</h5>
                <a href="/friends" class="btn btn-sm btn-neon-pink px-2 py-1 fs-7">MANAGE</a>
            </div>
            <div class="card-body p-0">
                <div class="list-group list-group-flush bg-transparent" id="dashboard-friends-list">
                    <!-- Dynamic friend rows will go here via JS -->
                    <div class="text-center py-4 text-muted-custom small" id="friends-empty-placeholder">
                        <i class="bi bi-person-x-fill fs-2 mb-2 d-block"></i>
                        No friends added yet.
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- RIGHT PANEL: GAME MODES & NOTIFICATION CENTRE -->
    <div class="col-lg-8 col-md-7">
        
        <!-- WELCOME HEADER -->
        <div class="bg-dark-card border-secondary p-4 mb-4 text-center text-md-start">
            <h2 class="fw-bold text-white mb-2">WELCOME TO THE GRID, <span class="text-neon-cyan">${currentUser.username}</span>!</h2>
            <p class="text-muted-custom mb-0">Initiate a session, input a game code, or challenge a friend to verify grid dominance.</p>
        </div>

        <!-- GAME ACTIONS -->
        <div class="row g-3 mb-4">
            <!-- CREATE GAME -->
            <div class="col-md-6">
                <div class="card bg-dark-card border-neon-cyan h-100 p-3 d-flex flex-column justify-content-between">
                    <div>
                        <div class="d-flex align-items-center mb-3">
                            <span class="fs-1 me-3">🌐</span>
                            <div>
                                <h5 class="fw-bold text-light m-0">CREATE PUBLIC LOBBY</h5>
                                <small class="text-muted-custom">Get a code to share with opponents</small>
                            </div>
                        </div>
                        <p class="text-muted-custom small">Generates a unique game code. Send it to a friend or let a public player enter it to match instantly.</p>
                    </div>
                    <a href="/game/create" class="btn btn-neon-cyan w-100 py-2 mt-2">
                        <i class="bi bi-plus-circle-fill me-2"></i>CREATE GAME
                    </a>
                </div>
            </div>

            <!-- JOIN GAME -->
            <div class="col-md-6">
                <div class="card bg-dark-card border-neon-pink h-100 p-3 d-flex flex-column justify-content-between">
                    <div>
                        <div class="d-flex align-items-center mb-3">
                            <span class="fs-1 me-3">🔑</span>
                            <div>
                                <h5 class="fw-bold text-light m-0">JOIN VIA GAME CODE</h5>
                                <small class="text-muted-custom">Enter code to join a room</small>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <input type="text" id="gameCodeInput" class="form-control form-control-dark text-center fw-bold fs-5 tracking-wide text-uppercase" placeholder="ENTER 6-CHAR CODE">
                            <div class="invalid-feedback text-center mt-1" id="join-game-error"></div>
                        </div>
                    </div>
                    <button onclick="joinGameSession()" class="btn btn-neon-pink w-100 py-2">
                        <i class="bi bi-box-arrow-in-right me-2"></i>JOIN GAME
                    </button>
                </div>
            </div>
        </div>

        <!-- NOTIFICATION CENTER -->
        <div class="card bg-dark-card border-secondary">
            <div class="card-header bg-transparent border-secondary d-flex justify-content-between align-items-center py-3">
                <h5 class="m-0 fw-bold text-light"><i class="bi bi-bell-fill me-2 text-neon-cyan"></i>LIVE ACTIVITY & NOTIFICATIONS</h5>
                <button onclick="markAllNotificationsRead()" class="btn btn-sm btn-outline-secondary text-muted-custom fs-7 py-1 px-2">MARK READ</button>
            </div>
            <div class="card-body p-0">
                <div class="list-group list-group-flush bg-transparent" id="dashboard-notifications-list">
                    <!-- Dynamic notifications will be appended here -->
                    <div class="text-center py-4 text-muted-custom small" id="notifications-empty-placeholder">
                        <i class="bi bi-bell-slash fs-2 mb-2 d-block"></i>
                        No new notifications.
                    </div>
                </div>
            </div>
        </div>
        
    </div>
</div>

<!-- Challange Request Modal -->
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

<!-- Global Toast Container -->
<div class="toast-container-custom" id="toastContainer"></div>

<!-- Dashboard Scripts -->
<script src="/static/js/dashboard.js"></script>

<%@ include file="footer.jsp" %>
