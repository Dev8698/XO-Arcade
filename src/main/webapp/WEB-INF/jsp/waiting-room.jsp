<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="navbar.jsp" %>

<div class="row justify-content-center">
    <div class="col-lg-6 col-md-8 text-center">
        
        <div class="card bg-dark-card border-neon-cyan p-5">
            <h2 class="fw-bold text-light mb-2">GRID WAITING ROOM</h2>
            <p class="text-muted-custom small mb-4">Share this room key. Match launches immediately upon opponent's entry.</p>

            <!-- GAME CODE CONTAINER -->
            <div class="mb-4">
                <span class="text-muted-custom d-block small uppercase mb-1">ROOM ACCESS KEY</span>
                <div class="d-flex justify-content-center align-items-center gap-2">
                    <span id="roomCode" class="fs-1 fw-extrabold tracking-wide text-neon-cyan bg-dark-glass border border-neon-cyan px-4 py-2 rounded" style="font-family: monospace;">
                        ${gameSession.gameCode}
                    </span>
                    <button class="btn btn-neon-cyan p-3" onclick="copyRoomCode()" title="Copy Code">
                        <i class="bi bi-copy fs-5"></i>
                    </button>
                </div>
            </div>

            <!-- WAITING ANIMATION -->
            <div class="py-4 my-2 position-relative d-flex justify-content-center align-items-center">
                <div class="spinner-grow text-neon-cyan" style="width: 5rem; height: 5rem;" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <div class="position-absolute text-neon-cyan fw-bold" style="font-size: 0.9rem;">WAITING</div>
            </div>

            <!-- OPPONENT STATUS -->
            <div class="mb-5">
                <h5 class="text-muted-custom" id="opponentStatus">
                    <i class="bi bi-person-fill-exclamation me-2"></i>Waiting for opponent to materialize...
                </h5>
            </div>

            <!-- CANCEL GAME BUTTON -->
            <button onclick="cancelLobby('${gameSession.id}')" class="btn btn-outline-danger w-100 py-3 text-neon-pink border-neon-pink">
                <i class="bi bi-x-circle-fill me-2"></i>ABORT LOBBY SESSION
            </button>
        </div>

    </div>
</div>

<!-- Accessing model attributes in JavaScript -->
<script>
    const gameSessionId = "${gameSession.id}";
</script>

<!-- Global Toast Container -->
<div class="toast-container-custom" id="toastContainer"></div>

<!-- Lobby Waiting Room JS -->
<script src="/static/js/lobby.js"></script>

<%@ include file="footer.jsp" %>
