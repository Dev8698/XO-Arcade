<%@ include file="navbar.jsp" %>

<div class="row justify-content-center">
    <div class="col-lg-6 col-md-8">
        
        <div class="card bg-dark-card border-neon-cyan p-4 pulse-glow text-center">
            <h3 class="fw-bold mb-3 text-neon-cyan"><i class="bi bi-controller me-2"></i>CREATE GAME SESSION</h3>
            <p class="text-muted-custom small mb-4">Initialize a secure session on the grid. You will receive a unique game code to share with your opponent.</p>
            
            <form action="/game/create" method="POST">
                <!-- Play Side Selection -->
                <div class="mb-4">
                    <label class="form-label text-light fw-semibold d-block mb-3">CHOOSE YOUR SYMBOL</label>
                    <div class="d-flex justify-content-center gap-4">
                        <div class="form-check form-check-inline p-0 m-0">
                            <input class="btn-check" type="radio" name="side" id="sideX" value="X" checked>
                            <label class="btn btn-outline-info px-4 py-3 fw-bold tracking-wide" for="sideX" style="width: 120px; font-size: 1.5rem;">
                                X
                            </label>
                        </div>
                        <div class="form-check form-check-inline p-0 m-0">
                            <input class="btn-check" type="radio" name="side" id="sideO" value="O" disabled>
                            <label class="btn btn-outline-secondary px-4 py-3 fw-bold tracking-wide text-muted" for="sideO" style="width: 120px; font-size: 1.5rem; opacity: 0.5;" title="Player O joined later">
                                O
                            </label>
                        </div>
                    </div>
                    <small class="text-muted-custom mt-2 d-block fs-8">Note: Session creators are assigned Symbol X by default.</small>
                </div>

                <!-- Match Rules Card -->
                <div class="card bg-dark-glass border-secondary p-3 mb-4 text-start">
                    <h6 class="fw-bold text-light mb-2"><i class="bi bi-shield-fill-check me-2 text-neon-pink"></i>ARENA RULES</h6>
                    <ul class="text-muted-custom small mb-0 ps-3">
                        <li>Each player has a turn to make a move.</li>
                        <li>Playing on occupied cells is strictly prohibited.</li>
                        <li>Winning conditions: 3 in a row (horizontal, vertical, or diagonal).</li>
                        <li>Connection dropout counts as immediate forfeit.</li>
                    </ul>
                </div>

                <!-- Launch Button -->
                <button type="submit" class="btn btn-neon-cyan w-100 py-3 d-flex align-items-center justify-content-center gap-2">
                    <i class="bi bi-rocket-takeoff-fill"></i>
                    <span>INITIALIZE GRID LOBBY</span>
                </button>
            </form>
            
            <a href="/dashboard" class="btn btn-outline-secondary w-100 mt-2 text-muted-custom">
                CANCEL
            </a>
        </div>
        
    </div>
</div>

<%@ include file="footer.jsp" %>
