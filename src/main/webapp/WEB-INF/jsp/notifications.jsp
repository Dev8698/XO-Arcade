<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="navbar.jsp" %>

<div class="row justify-content-center">
    <div class="col-lg-8 col-md-10">
        
        <!-- HEADER AND QUICK ACTION -->
        <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom border-secondary">
            <h3 class="fw-bold text-neon-cyan m-0">
                <i class="bi bi-bell-fill me-2"></i>NOTIFICATIONS
            </h3>
            <c:if test="${not empty notifications}">
                <button onclick="markAllNotificationsReadPage()" class="btn btn-sm btn-neon-pink">
                    <i class="bi bi-check-all me-1"></i> MARK ALL READ
                </button>
            </c:if>
        </div>

        <!-- NOTIFICATION CONTAINER -->
        <div class="card bg-dark-card border-secondary mb-4">
            <div class="card-body p-0">
                <div class="list-group list-group-flush bg-transparent" id="notifications-page-list">
                    <c:choose>
                        <c:when test="${empty notifications}">
                            <div class="text-center text-muted-custom py-5">
                                <i class="bi bi-bell-slash fs-1 text-secondary mb-3 d-block"></i>
                                <h5>Your Grid is Clear</h5>
                                <p class="small mb-0">No alerts or messages to display. Play a game or add a friend to start receiving updates.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="notif" items="${notifications}">
                                <div class="list-group-item bg-transparent border-secondary text-light py-4 px-4 d-flex align-items-start justify-content-between ${notif.readStatus ? '' : 'bg-dark-glass border-start border-3 border-neon-cyan'}">
                                    <div class="d-flex align-items-start gap-3">
                                        <!-- Notification Type Icon -->
                                        <div class="fs-4">
                                            <c:choose>
                                                <c:when test="${notif.type == 'FRIEND_REQUEST'}">👤</c:when>
                                                <c:when test="${notif.type == 'FRIEND_ACCEPT'}">🤝</c:when>
                                                <c:when test="${notif.type == 'GAME_INVITATION'}">⚔️</c:when>
                                                <c:when test="${notif.type == 'MATCH_RESULT'}">🏆</c:when>
                                                <c:otherwise>🔔</c:otherwise>
                                            </c:choose>
                                        </div>
                                        
                                        <div>
                                            <p class="m-0 text-white fw-semibold small">${notif.message}</p>
                                            <span class="text-muted-custom fs-8">${notif.createdTime.toLocalDate()} at ${notif.createdTime.toLocalTime().toString().substring(0, 5)}</span>
                                            
                                            <!-- Action Buttons depending on type and status -->
                                            <c:if test="${!notif.readStatus}">
                                                <c:choose>
                                                    <c:when test="${notif.type == 'FRIEND_REQUEST'}">
                                                        <div class="mt-2 d-flex gap-2">
                                                            <button class="btn btn-sm btn-neon-cyan px-3 py-1 fs-8" onclick="acceptRequestFromPage(${notif.id})">ACCEPT</button>
                                                            <button class="btn btn-sm btn-outline-secondary px-3 py-1 fs-8 text-muted-custom" onclick="rejectRequestFromPage(${notif.id})">REJECT</button>
                                                        </div>
                                                    </c:when>
                                                    <c:when test="${notif.type == 'GAME_INVITATION'}">
                                                        <div class="mt-2 d-flex gap-2">
                                                            <button class="btn btn-sm btn-neon-cyan px-3 py-1 fs-8" onclick="acceptChallengeFromPage(${notif.id})">ACCEPT MATCH</button>
                                                            <button class="btn btn-sm btn-outline-secondary px-3 py-1 fs-8 text-muted-custom" onclick="rejectChallengeFromPage(${notif.id})">DECLINE</button>
                                                        </div>
                                                    </c:when>
                                                </c:choose>
                                            </c:if>
                                        </div>
                                    </div>
                                    
                                    <!-- Read Status Indicator / Individual Mark Read -->
                                    <c:if test="${!notif.readStatus}">
                                        <button class="btn btn-link text-neon-cyan p-0 text-decoration-none fs-8" onclick="markSingleReadFromPage(${notif.id})">
                                            <i class="bi bi-check-lg me-1"></i> Mark read
                                        </button>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div class="text-center">
            <a href="/dashboard" class="btn btn-neon-cyan py-2 px-4">
                <i class="bi bi-grid-1x2-fill me-2"></i> Back to Dashboard
            </a>
        </div>
        
    </div>
</div>

<!-- Modal Dialog for real-time challenge receive -->
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

<!-- Notifications JS -->
<script src="/static/js/notifications.js"></script>

<%@ include file="footer.jsp" %>
