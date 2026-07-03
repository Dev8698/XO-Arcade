<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="navbar.jsp" %>

<div class="row justify-content-center">
    <div class="col-lg-10">
        
        <!-- HEADER AND FILTER BAR -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 pb-2 border-bottom border-secondary gap-3">
            <h3 class="fw-bold text-neon-cyan m-0">
                <i class="bi bi-clock-history me-2"></i>MATCH HISTORIES
            </h3>
            
            <!-- Filters Tabs -->
            <div class="btn-group bg-dark-glass p-1 rounded border border-secondary" role="group" aria-label="Outcome Filters">
                <a href="/history?filter=ALL" class="btn btn-sm ${selectedFilter == 'ALL' ? 'btn-neon-cyan' : 'btn-outline-dark text-muted-custom border-0'} px-3 py-2">
                    ALL
                </a>
                <a href="/history?filter=WIN" class="btn btn-sm ${selectedFilter == 'WIN' ? 'btn-neon-cyan' : 'btn-outline-dark text-muted-custom border-0'} px-3 py-2">
                    WINS
                </a>
                <a href="/history?filter=LOSS" class="btn btn-sm ${selectedFilter == 'LOSS' ? 'btn-neon-pink' : 'btn-outline-dark text-muted-custom border-0'} px-3 py-2">
                    LOSSES
                </a>
                <a href="/history?filter=DRAW" class="btn btn-sm ${selectedFilter == 'DRAW' ? 'btn-neon-purple' : 'btn-outline-dark text-muted-custom border-0'} px-3 py-2">
                    DRAWS
                </a>
            </div>
        </div>

        <!-- HISTORY DATA LIST -->
        <div class="card bg-dark-card border-secondary">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-dark table-striped table-hover align-middle mb-0 text-center" style="--bs-table-bg: transparent; --bs-table-striped-bg: rgba(255,255,255,0.02);">
                        <thead>
                            <tr class="border-secondary text-muted-custom small">
                                <th scope="col" class="py-3">DATE / TIME</th>
                                <th scope="col" class="py-3">OPPONENT</th>
                                <th scope="col" class="py-3">RESULT</th>
                                <th scope="col" class="py-3">MOVES</th>
                                <th scope="col" class="py-3">WINNER</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty historyList}">
                                    <tr>
                                        <td colspan="5" class="py-5 text-muted-custom">
                                            <i class="bi bi-calendar-x fs-1 text-secondary mb-3 d-block"></i>
                                            <h5>No Records Found</h5>
                                            <p class="small mb-0">Matches you play on the grid will be archived here.</p>
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="match" items="${historyList}">
                                        <tr class="border-secondary">
                                            <td class="py-3 text-muted-custom small">
                                                ${match.playedDate.toLocalDate()} <br>
                                                <span class="fs-8">${match.playedDate.toLocalTime().toString().substring(0, 5)}</span>
                                            </td>
                                            <td class="py-3">
                                                <div class="d-flex align-items-center justify-content-center gap-2">
                                                    <img src="${match.opponent.avatar != null ? match.opponent.avatar : 'https://robohash.org/opponent.png?set=set4'}" class="rounded-circle" style="width: 32px; height: 32px;">
                                                    <span class="fw-semibold text-white small">${match.opponent.username}</span>
                                                </div>
                                            </td>
                                            <td class="py-3">
                                                <c:choose>
                                                    <c:when test="${match.result == 'WIN'}">
                                                        <span class="badge badge-win px-3 py-2 uppercase fw-bold tracking-wide">VICTORY</span>
                                                    </c:when>
                                                    <c:when test="${match.result == 'LOSS'}">
                                                        <span class="badge badge-loss px-3 py-2 uppercase fw-bold tracking-wide">DEFEAT</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-draw px-3 py-2 uppercase fw-bold tracking-wide">DRAW</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="py-3 fw-bold text-light">
                                                ${match.movesCount}
                                            </td>
                                            <td class="py-3 text-muted-custom small">
                                                <c:choose>
                                                    <c:when test="${match.winner != null}">
                                                        <span class="text-neon-cyan fw-semibold">${match.winner.username}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">—</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="text-center mt-4">
            <a href="/dashboard" class="btn btn-neon-cyan py-2 px-4">
                <i class="bi bi-grid-1x2-fill me-2"></i> Back to Dashboard
            </a>
        </div>
        
    </div>
</div>

<%@ include file="footer.jsp" %>
