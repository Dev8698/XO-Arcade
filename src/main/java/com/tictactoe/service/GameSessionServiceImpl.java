package com.tictactoe.service;

import com.tictactoe.entity.GameSession;
import com.tictactoe.entity.Notification;
import com.tictactoe.entity.User;
import com.tictactoe.repository.GameSessionRepository;
import com.tictactoe.repository.UserRepository;
import com.tictactoe.util.GameCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class GameSessionServiceImpl implements GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameSessionServiceImpl(GameSessionRepository gameSessionRepository,
                                  UserRepository userRepository,
                                  NotificationService notificationService,
                                  SimpMessagingTemplate messagingTemplate) {
        this.gameSessionRepository = gameSessionRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public GameSession createGameSession(String creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator user not found."));

        // Generate a unique 6-character game code, checking for collisions
        String code;
        do {
            code = GameCodeGenerator.generateCode();
        } while (gameSessionRepository.findByGameCode(code).isPresent());

        GameSession session = GameSession.builder()
                .id(UUID.randomUUID().toString())
                .gameCode(code)
                .playerX(creator)
                .status("WAITING")
                .boardState(",,,,,,,,")
                .build();

        return gameSessionRepository.save(session);
    }

    @Override
    public GameSession joinGameSession(String playerId, String gameCode) {
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player user not found."));

        GameSession session = gameSessionRepository.findByGameCode(gameCode.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Game Code."));

        if (!"WAITING".equals(session.getStatus())) {
            throw new IllegalStateException("Game room is no longer open for joining.");
        }

        if (session.getPlayerX().getId().equals(playerId)) {
            throw new IllegalArgumentException("You cannot join your own game.");
        }

        session.setPlayerO(player);
        session.setStatus("PLAYING");
        session.setCurrentTurn(session.getPlayerX()); // Player X goes first

        GameSession savedSession = gameSessionRepository.save(session);

        // Broadcast session state update via WebSocket to anyone listening in the waiting room
        try {
            messagingTemplate.convertAndSend("/topic/game/" + savedSession.getId(), savedSession);
        } catch (Exception e) {
            System.err.println("Lobby broadcast failed: " + e.getMessage());
        }

        return savedSession;
    }

    @Override
    @Transactional(readOnly = true)
    public GameSession getGameSession(String id) {
        return gameSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game session not found with ID: " + id));
    }

    @Override
    public void cancelGameSession(String id, String playerId) {
        GameSession session = gameSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game session not found."));

        if (!session.getPlayerX().getId().equals(playerId)) {
            throw new IllegalStateException("Only the host can cancel this game.");
        }

        if (!"WAITING".equals(session.getStatus())) {
            throw new IllegalStateException("Cannot cancel a game that has already started.");
        }

        gameSessionRepository.delete(session);
    }

    @Override
    public void leaveGameSession(String id, String playerId) {
        GameSession session = gameSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game session not found."));

        if (!"PLAYING".equals(session.getStatus())) {
            return; // Already completed or not started
        }

        User winner = null;
        if (session.getPlayerX().getId().equals(playerId)) {
            winner = session.getPlayerO();
        } else if (session.getPlayerO() != null && session.getPlayerO().getId().equals(playerId)) {
            winner = session.getPlayerX();
        }

        if (winner != null) {
            session.setStatus("WON");
            session.setWinner(winner);
            gameSessionRepository.save(session);

            // Broadcast opponent left status to the game topic
            try {
                messagingTemplate.convertAndSend("/topic/game/" + id, session);
            } catch (Exception e) {
                System.err.println("Broadcast leave failed: " + e.getMessage());
            }
        }
    }

    @Override
    public void challengeFriend(String challengerId, String friendId) {
        User challenger = userRepository.findById(challengerId)
                .orElseThrow(() -> new RuntimeException("Challenger user not found."));

        // Create waiting game session
        GameSession session = createGameSession(challengerId);

        // Send game invitation notification
        notificationService.createNotification(
                "GAME_INVITATION",
                challengerId,
                friendId,
                challenger.getUsername() + " has challenged you to a Tic Tac Toe match!",
                session.getId()
        );
    }

    @Override
    public GameSession acceptChallenge(Long notificationId) {
        Notification notif = notificationService.getNotificationById(notificationId);
        String gameId = notif.getGameSessionId();
        
        if (gameId == null) {
            throw new IllegalArgumentException("Notification does not contain a game session link.");
        }

        GameSession session = gameSessionRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game session not found."));

        if (!"WAITING".equals(session.getStatus())) {
            throw new IllegalStateException("Game room is no longer open.");
        }

        User friend = notif.getReceiver();
        session.setPlayerO(friend);
        session.setStatus("PLAYING");
        session.setCurrentTurn(session.getPlayerX());

        GameSession savedSession = gameSessionRepository.save(session);
        notificationService.markAsRead(notificationId);

        // Send GAME_START notification to the challenger to trigger their browser redirect
        try {
            notificationService.createNotification(
                    "GAME_START",
                    friend.getId(),
                    session.getPlayerX().getId(),
                    friend.getUsername() + " accepted your challenge! Game starting...",
                    session.getId()
            );
        } catch (Exception e) {
            System.err.println("Failed to send GAME_START notification to challenger: " + e.getMessage());
        }

        // Broadcast to trigger instant game board transition for both players
        try {
            messagingTemplate.convertAndSend("/topic/game/" + savedSession.getId(), savedSession);
        } catch (Exception e) {
            System.err.println("Challenge accept broadcast failed: " + e.getMessage());
        }

        return savedSession;
    }

    @Override
    public void rejectChallenge(Long notificationId) {
        Notification notif = notificationService.getNotificationById(notificationId);
        String gameId = notif.getGameSessionId();
        
        if (gameId != null) {
            gameSessionRepository.findById(gameId).ifPresent(gameSessionRepository::delete);
        }
        notificationService.markAsRead(notificationId);
    }
}
