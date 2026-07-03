<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="navbar.jsp" %>

<!-- Game Board Styling -->
<div class="row justify-content-center g-4">
    <!-- PLAYERS STATUS BAR -->
    <div class="col-12 col-md-10 text-center mb-2">
        <div class="d-flex justify-content-between align-items-center bg-dark-glass border border-secondary p-3 rounded-pill px-4 shadow">
            
            <!-- PLAYER X CARD -->
            <div class="d-flex align-items-center gap-2 text-start">
                <img src="${gameSession.playerX.avatar != null ? gameSession.playerX.avatar : 'https://robohash.org/playerx.png?set=set4'}" 
                     class="rounded-circle border border-2 border-neon-cyan" 
                     style="width: 45px; height: 45px; object-fit: cover;">
                <div>
                    <span class="fw-bold text-neon-cyan d-block">${gameSession.playerX.username}</span>
                    <span class="text-muted-custom fs-8">SYMBOL: X</span>
                </div>
            </div>

            <!-- VS AND STATUS -->
            <div>
                <span class="badge bg-dark-glass border border-secondary text-muted-custom px-3 py-2 fs-6 mx-2">
                    VS
                </span>
                <span id="turnIndicator" class="badge bg-info text-dark px-3 py-2 fs-6 pulse-glow">
                    WAITING...
                </span>
            </div>

            <!-- PLAYER O CARD -->
            <div class="d-flex align-items-center gap-2 text-end flex-row-reverse">
                <img src="${gameSession.playerO.avatar != null ? gameSession.playerO.avatar : 'https://robohash.org/playero.png?set=set4'}" 
                     class="rounded-circle border border-2 border-neon-pink" 
                     style="width: 45px; height: 45px; object-fit: cover;">
                <div>
                    <span class="fw-bold text-neon-pink d-block">${gameSession.playerO.username}</span>
                    <span class="text-muted-custom fs-8">SYMBOL: O</span>
                </div>
            </div>

        </div>
    </div>

    <!-- MAIN GAME PANEL -->
    <div class="col-12 col-md-6 col-lg-5">
        
        <!-- BOARD CARD -->
        <div class="card bg-dark-card border-secondary p-4 mb-4 text-center">
            
            <!-- GRID -->
            <div class="game-grid mb-4" id="boardGrid">
                <div class="game-cell" data-index="0" onclick="makeCellMove(0)"></div>
                <div class="game-cell" data-index="1" onclick="makeCellMove(1)"></div>
                <div class="game-cell" data-index="2" onclick="makeCellMove(2)"></div>
                <div class="game-cell" data-index="3" onclick="makeCellMove(3)"></div>
                <div class="game-cell" data-index="4" onclick="makeCellMove(4)"></div>
                <div class="game-cell" data-index="5" onclick="makeCellMove(5)"></div>
                <div class="game-cell" data-index="6" onclick="makeCellMove(6)"></div>
                <div class="game-cell" data-index="7" onclick="makeCellMove(7)"></div>
                <div class="game-cell" data-index="8" onclick="makeCellMove(8)"></div>
            </div>

            <!-- MATCH ACTIONS -->
            <div class="d-flex gap-2">
                <button onclick="leaveGameMatch()" class="btn btn-outline-danger w-100 py-2">
                    <i class="bi bi-box-arrow-left me-1"></i> FORFEIT MATCH
                </button>
            </div>

        </div>

    </div>
</div>

<!-- MATCH END MODAL -->
<div class="modal fade" id="matchEndModal" data-bs-backdrop="static" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content bg-dark-card text-light border-neon-cyan" id="modalBorderContainer">
            <div class="modal-body text-center py-5">
                <h1 class="fw-extrabold text-neon-cyan mb-3" id="matchEndTitle">VICTORY!</h1>
                <p class="text-muted-custom fs-5 mb-4" id="matchEndMessage">You have successfully dominated the grid.</p>
                <div class="d-flex flex-column gap-2 px-4">
                    <button class="btn btn-neon-cyan py-3" onclick="requestRematch()" id="rematchBtn">PLAY AGAIN</button>
                    <a href="/dashboard" class="btn btn-outline-secondary py-2 text-muted-custom">BACK TO DASHBOARD</a>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Global variables to export into JavaScript -->
<script>
    const currentUserId = "${currentUser.id}";
    const sessionId = "${gameSession.id}";
    const playerXId = "${gameSession.playerX.id}";
    const playerOId = "${gameSession.playerO != null ? gameSession.playerO.id : ''}";
    
    // Export initial game state attributes
    const initialBoardState = "${gameSession.boardState}";
    const initialGameStatus = "${gameSession.status}";
    const initialCurrentTurnId = "${gameSession.currentTurn != null ? gameSession.currentTurn.id : ''}";
    const initialWinnerId = "${gameSession.winner != null ? gameSession.winner.id : ''}";
</script>

<!-- Global Toast Container -->
<div class="toast-container-custom" id="toastContainer"></div>

<!-- Gameplay JS -->
<script src="/static/js/game.js"></script>

<%@ include file="footer.jsp" %>
