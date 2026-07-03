<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="navbar.jsp" %>

<div class="row g-4">
    <!-- LEFT SIDE: SEARCH & PENDING REQUESTS -->
    <div class="col-md-5">
        
        <!-- SEARCH CARD -->
        <div class="card bg-dark-card border-neon-cyan mb-4">
            <div class="card-header bg-transparent border-secondary py-3">
                <h5 class="m-0 fw-bold text-light"><i class="bi bi-search me-2 text-neon-cyan"></i>SEARCH PLAYERS</h5>
            </div>
            <div class="card-body">
                <div class="input-group mb-3">
                    <input type="text" id="searchPlayerInput" class="form-control form-control-dark" placeholder="Enter username...">
                    <button class="btn btn-neon-cyan" type="button" onclick="searchPlayers()"><i class="bi bi-search"></i></button>
                </div>
                
                <!-- Search Results Container -->
                <div id="search-results-list" class="list-group list-group-flush bg-transparent">
                    <div class="text-center text-muted-custom py-3 small">Enter a name to query the grid.</div>
                </div>
            </div>
        </div>

        <!-- PENDING REQUESTS CARD -->
        <div class="card bg-dark-card border-purple">
            <div class="card-header bg-transparent border-secondary py-3">
                <h5 class="m-0 fw-bold text-light"><i class="bi bi-person-plus-fill me-2 text-purple" style="color:#b388ff !important;"></i>PENDING REQUESTS</h5>
            </div>
            <div class="card-body p-0">
                <div class="list-group list-group-flush bg-transparent" id="pending-requests-list">
                    <c:choose>
                        <c:when test="${empty pendingRequests}">
                            <div class="text-center text-muted-custom py-4 small">
                                <i class="bi bi-check-circle fs-3 mb-2 d-block"></i> No pending invitations.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="req" items="${pendingRequests}">
                                <div class="list-group-item bg-transparent border-secondary text-light d-flex align-items-center justify-content-between py-3 px-3">
                                    <div class="d-flex align-items-center">
                                        <img src="${req.sender.avatar != null ? req.sender.avatar : 'https://robohash.org/guest.png?set=set4'}" class="rounded-circle me-2" style="width:35px; height:35px;">
                                        <span class="fw-semibold small">${req.sender.username}</span>
                                    </div>
                                    <div class="d-flex gap-1">
                                        <button class="btn btn-sm btn-neon-cyan px-2 py-1 fs-8" onclick="acceptFriendRequestDirect(${req.id})">ACCEPT</button>
                                        <button class="btn btn-sm btn-outline-secondary px-2 py-1 fs-8 text-muted-custom" onclick="rejectFriendRequestDirect(${req.id})">DECLINE</button>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

    </div>

    <!-- RIGHT SIDE: ACTIVE FRIENDS LIST -->
    <div class="col-md-7">
        <div class="card bg-dark-card border-neon-pink">
            <div class="card-header bg-transparent border-secondary py-3">
                <h5 class="m-0 fw-bold text-light"><i class="bi bi-people-fill me-2 text-neon-pink"></i>MY FRIENDS (${friendsCount != null ? friendsCount : 0})</h5>
            </div>
            <div class="card-body p-0">
                <div class="list-group list-group-flush bg-transparent" id="friends-list-container">
                    <c:choose>
                        <c:when test="${empty friends}">
                            <div class="text-center text-muted-custom py-5">
                                <i class="bi bi-person-x fs-1 mb-2 d-block"></i>
                                <h5>No Friends Yet</h5>
                                <p class="small">Use the search box on the left to add players to your friend circle.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="friend" items="${friends}">
                                <div class="list-group-item bg-transparent border-secondary text-light d-flex align-items-center justify-content-between py-3 px-3">
                                    <div class="d-flex align-items-center">
                                        <div class="position-relative">
                                            <img src="${friend.avatar != null ? friend.avatar : 'https://robohash.org/guest.png?set=set4'}" class="rounded-circle me-2" style="width:45px; height:45px; border: 1px solid rgba(138,43,226,0.3)">
                                            <span class="position-absolute bottom-0 end-0 p-1 rounded-circle border border-dark ${friend.online ? 'bg-success' : 'bg-secondary'}" style="width:10px; height:10px;"></span>
                                        </div>
                                        <div>
                                            <span class="fw-semibold d-block text-white">${friend.username}</span>
                                            <span class="text-muted-custom fs-8">${friend.online ? 'ONLINE' : 'OFFLINE'}</span>
                                        </div>
                                    </div>
                                    
                                    <div class="d-flex gap-2">
                                        <c:choose>
                                            <c:when test="${friend.online}">
                                                <button class="btn btn-sm btn-neon-cyan py-1 px-3 fs-8" onclick="challengeFromFriendsPage('${friend.id}')">
                                                    <i class="bi bi-sword me-1"></i> CHALLENGE
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <button class="btn btn-sm btn-outline-secondary py-1 px-3 fs-8 text-muted-custom" disabled>
                                                    CHALLENGE
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                        <button class="btn btn-sm btn-outline-danger py-1 px-2 fs-8" onclick="removeFriend('${friend.id}')">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
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

<!-- Friends List JS -->
<script src="/static/js/friends.js?v=2"></script>

<%@ include file="footer.jsp" %>
