<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="navbar.jsp" %>

<div class="row justify-content-center">
    <div class="col-lg-8 col-md-10">
        
        <!-- Profile Gamer Card -->
        <div class="card bg-dark-card p-4 border-neon-cyan mb-4">
            <div class="card-body">
                <div class="row align-items-center text-center text-md-start">
                    <!-- Avatar Column -->
                    <div class="col-md-3 mb-3 mb-md-0 d-flex justify-content-center">
                        <div class="position-relative">
                            <img src="${currentUser.avatar != null ? currentUser.avatar : 'https://robohash.org/guest.png?set=set4'}" 
                                 alt="Gamer Avatar" 
                                 class="rounded-circle border border-3 border-neon-cyan shadow-lg"
                                 style="width: 140px; height: 140px; object-fit: cover;">
                            <span class="position-absolute bottom-0 end-0 p-2 bg-success border border-2 border-dark rounded-circle pulse-glow" title="Online"></span>
                        </div>
                    </div>
                    
                    <!-- Details Column -->
                    <div class="col-md-9">
                        <div class="d-flex flex-column flex-md-row justify-content-between align-items-center align-items-md-start mb-2">
                            <div>
                                <h2 class="fw-bold text-neon-cyan m-0">${currentUser.username}</h2>
                                <p class="text-muted-custom small mb-2">Member since: ${currentUser.createdAt.toLocalDate()}</p>
                            </div>
                            <span class="badge bg-dark-glass border border-neon-pink text-neon-pink px-3 py-2 fs-6">
                                <i class="bi bi-controller me-1"></i> PLAYER CARD
                            </span>
                        </div>
                        
                        <div class="mb-3 text-muted-custom">
                            <i class="bi bi-envelope-fill me-2 text-neon-cyan"></i>${currentUser.email}
                        </div>
                        
                        <div class="d-flex flex-wrap gap-2 justify-content-center justify-content-md-start">
                            <span class="badge bg-dark border border-secondary text-light px-3 py-2">
                                <i class="bi bi-people-fill me-1 text-neon-cyan"></i> Friends: ${friendsCount != null ? friendsCount : 0}
                            </span>
                            <span class="badge bg-dark border border-secondary text-light px-3 py-2">
                                <i class="bi bi-trophy-fill me-1 text-warning"></i> Rank: Challenger
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Statistics & Performance Cards -->
        <h4 class="text-neon-pink fw-bold mb-3"><i class="bi bi-bar-chart-line-fill me-2"></i>STATISTICS & METRICS</h4>
        <div class="row g-3 mb-4">
            <!-- Games Played -->
            <div class="col-sm-6 col-md-3">
                <div class="card bg-dark-card border-secondary text-center p-3">
                    <div class="fs-1 fw-bold text-white">${currentUser.gamesPlayed}</div>
                    <div class="text-muted-custom small uppercase fw-semibold">GAMES PLAYED</div>
                </div>
            </div>
            <!-- Wins -->
            <div class="col-sm-6 col-md-3">
                <div class="card bg-dark-card border-neon-cyan text-center p-3" style="box-shadow: 0 0 5px rgba(0, 240, 255, 0.2);">
                    <div class="fs-1 fw-bold text-neon-cyan">${currentUser.wins}</div>
                    <div class="text-neon-cyan small uppercase fw-semibold">WINS 🏆</div>
                </div>
            </div>
            <!-- Losses -->
            <div class="col-sm-6 col-md-3">
                <div class="card bg-dark-card border-neon-pink text-center p-3" style="box-shadow: 0 0 5px rgba(255, 0, 127, 0.2);">
                    <div class="fs-1 fw-bold text-neon-pink">${currentUser.losses}</div>
                    <div class="text-neon-pink small uppercase fw-semibold">LOSSES ❌</div>
                </div>
            </div>
            <!-- Draws -->
            <div class="col-sm-6 col-md-3">
                <div class="card bg-dark-card border-purple text-center p-3">
                    <div class="fs-1 fw-bold text-purple" style="color: #b388ff !important;">${currentUser.draws}</div>
                    <div class="text-purple small uppercase fw-semibold" style="color: #b388ff !important;">DRAWS 🤝</div>
                </div>
            </div>
        </div>

        <!-- Performance / Win Rate Ratio -->
        <div class="card bg-dark-card p-4 border-purple mb-4">
            <h5 class="fw-semibold mb-3 text-light">Arena Win Rate Ratio</h5>
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="text-muted-custom">Win Percentage</span>
                <span class="fw-bold text-neon-cyan">${currentUser.winPercentage}%</span>
            </div>
            <div class="progress bg-dark-glass border border-secondary" style="height: 16px; border-radius: 8px;">
                <div class="progress-bar bg-info progress-bar-striped progress-bar-animated shadow" 
                     role="progressbar" 
                     style="width: ${currentUser.winPercentage}%" 
                     aria-valuenow="${currentUser.winPercentage}" 
                     aria-valuemin="0" 
                     aria-valuemax="100">
                </div>
            </div>
            <div class="mt-3 text-muted-custom small text-center">
                Maximize victories to climb the leaderboards and prove your dominance on the grid.
            </div>
        </div>

        <!-- Quick actions -->
        <div class="text-center mt-3">
            <a href="/dashboard" class="btn btn-neon-cyan me-2">
                <i class="bi bi-grid-1x2-fill me-2"></i> Go to Dashboard
            </a>
            <a href="/friends" class="btn btn-neon-pink">
                <i class="bi bi-people-fill me-2"></i> View Friends
            </a>
        </div>
        
    </div>
</div>

<%@ include file="footer.jsp" %>
