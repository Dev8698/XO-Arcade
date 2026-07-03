package com.tictactoe.service;

import com.tictactoe.entity.GameMove;
import com.tictactoe.entity.GameSession;
import com.tictactoe.entity.MatchHistory;
import com.tictactoe.entity.User;
import com.tictactoe.repository.GameMoveRepository;
import com.tictactoe.repository.GameSessionRepository;
import com.tictactoe.repository.MatchHistoryRepository;
import com.tictactoe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GamePlayServiceImpl implements GamePlayService {

    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;
    private final GameMoveRepository gameMoveRepository;
    private final MatchHistoryRepository matchHistoryRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GamePlayServiceImpl(GameSessionRepository gameSessionRepository,
                               UserRepository userRepository,
                               GameMoveRepository gameMoveRepository,
                               MatchHistoryRepository matchHistoryRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.gameSessionRepository = gameSessionRepository;
        this.userRepository = userRepository;
        this.gameMoveRepository = gameMoveRepository;
        this.matchHistoryRepository = matchHistoryRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public GameSession makeMove(String gameId, String playerId, int cellIndex) {
        GameSession session = gameSessionRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game session not found."));

        // Validation: Must be active play state
        if (!"PLAYING".equals(session.getStatus())) {
            throw new IllegalStateException("Game is not in active PLAYING state.");
        }

        // Validation: Correct player turn
        if (!session.getCurrentTurn().getId().equals(playerId)) {
            throw new IllegalStateException("It is not your turn.");
        }

        // Validation: Valid grid position
        if (cellIndex < 0 || cellIndex > 8) {
            throw new IllegalArgumentException("Grid cells must be in index range 0-8.");
        }

        // Parse board cells
        String[] cells = session.getBoardState().split(",", -1);
        if (cells.length != 9) {
            throw new IllegalStateException("Internal error: board cell count mismatch.");
        }

        // Validation: Cell occupancy
        if (!cells[cellIndex].isEmpty()) {
            throw new IllegalArgumentException("Cell position is already occupied.");
        }

        // Assign symbol and write to array
        boolean isPlayerX = session.getPlayerX().getId().equals(playerId);
        String symbol = isPlayerX ? "X" : "O";
        cells[cellIndex] = symbol;

        // Reconstruct boardState string
        String updatedBoardState = String.join(",", cells);
        session.setBoardState(updatedBoardState);

        // Record Move in DB
        User player = isPlayerX ? session.getPlayerX() : session.getPlayerO();
        GameMove move = GameMove.builder()
                .gameSession(session)
                .player(player)
                .moveIndex(cellIndex)
                .symbol(symbol)
                .build();
        gameMoveRepository.save(move);

        // Count total moves made so far
        List<GameMove> allMoves = gameMoveRepository.findByGameSession_IdOrderByCreatedTimeAsc(gameId);
        int movesCount = allMoves.size() + 1; // plus current move

        // Check for Win or Draw
        if (checkWinPattern(cells, symbol)) {
            // WIN DETECTED
            User winner = player;
            User loser = isPlayerX ? session.getPlayerO() : session.getPlayerX();

            session.setStatus("WON");
            session.setWinner(winner);

            // Update local user profiles stats
            winner.setWins(winner.getWins() + 1);
            winner.setGamesPlayed(winner.getGamesPlayed() + 1);
            loser.setLosses(loser.getLosses() + 1);
            loser.setGamesPlayed(loser.getGamesPlayed() + 1);

            userRepository.save(winner);
            userRepository.save(loser);

            // Record double-entry Match History (one from each player's perspective)
            MatchHistory histWinner = MatchHistory.builder()
                    .gameId(gameId)
                    .user(winner)
                    .opponent(loser)
                    .winner(winner)
                    .movesCount(movesCount)
                    .result("WIN")
                    .build();

            MatchHistory histLoser = MatchHistory.builder()
                    .gameId(gameId)
                    .user(loser)
                    .opponent(winner)
                    .winner(winner)
                    .movesCount(movesCount)
                    .result("LOSS")
                    .build();

            matchHistoryRepository.save(histWinner);
            matchHistoryRepository.save(histLoser);

        } else if (isBoardFull(cells)) {
            // DRAW DETECTED
            session.setStatus("DRAW");
            session.setWinner(null);

            User playerX = session.getPlayerX();
            User playerO = session.getPlayerO();

            // Update stats
            playerX.setDraws(playerX.getDraws() + 1);
            playerX.setGamesPlayed(playerX.getGamesPlayed() + 1);
            playerO.setDraws(playerO.getDraws() + 1);
            playerO.setGamesPlayed(playerO.getGamesPlayed() + 1);

            userRepository.save(playerX);
            userRepository.save(playerO);

            // Record history
            MatchHistory histX = MatchHistory.builder()
                    .gameId(gameId)
                    .user(playerX)
                    .opponent(playerO)
                    .movesCount(movesCount)
                    .result("DRAW")
                    .build();

            MatchHistory histO = MatchHistory.builder()
                    .gameId(gameId)
                    .user(playerO)
                    .opponent(playerX)
                    .movesCount(movesCount)
                    .result("DRAW")
                    .build();

            matchHistoryRepository.save(histX);
            matchHistoryRepository.save(histO);

        } else {
            // Game continues: switch active player turn
            session.setCurrentTurn(isPlayerX ? session.getPlayerO() : session.getPlayerX());
        }

        GameSession savedSession = gameSessionRepository.save(session);

        // Broadcast game updates to subscribers on topic /topic/game/{gameId}
        try {
            messagingTemplate.convertAndSend("/topic/game/" + gameId, savedSession);
        } catch (Exception e) {
            System.err.println("Failed to broadcast game move updates: " + e.getMessage());
        }

        return savedSession;
    }

    private boolean checkWinPattern(String[] cells, String symbol) {
        int[][] winPatterns = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Horizontal rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Vertical columns
            {0, 4, 8}, {2, 4, 6}             // Diagonals
        };

        for (int[] pattern : winPatterns) {
            if (cells[pattern[0]].equals(symbol) &&
                cells[pattern[1]].equals(symbol) &&
                cells[pattern[2]].equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBoardFull(String[] cells) {
        for (String cell : cells) {
            if (cell.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
