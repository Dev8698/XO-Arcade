package com.tictactoe.controller;

import com.tictactoe.entity.GameSession;
import com.tictactoe.security.AuthenticatedUser;
import com.tictactoe.service.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameApiController {

    private final GameSessionService gameSessionService;

    @Autowired
    public GameApiController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinGame(@AuthenticationPrincipal AuthenticatedUser authUser,
                                      @RequestParam String code) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            GameSession session = gameSessionService.joinGameSession(authUser.getId(), code);
            return ResponseEntity.ok(session);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred.");
        }
    }

    @PostMapping("/challenge/{notificationId}/accept")
    public ResponseEntity<Map<String, String>> acceptChallenge(@AuthenticationPrincipal AuthenticatedUser authUser,
                                                               @PathVariable Long notificationId) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            GameSession session = gameSessionService.acceptChallenge(notificationId);
            Map<String, String> response = new HashMap<>();
            response.put("gameId", session.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/challenge/{notificationId}/reject")
    public ResponseEntity<Void> rejectChallenge(@AuthenticationPrincipal AuthenticatedUser authUser,
                                                @PathVariable Long notificationId) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            gameSessionService.rejectChallenge(notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelGame(@AuthenticationPrincipal AuthenticatedUser authUser,
                                           @PathVariable String id) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            gameSessionService.cancelGameSession(id, authUser.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/leave/{id}")
    public ResponseEntity<Void> leaveGame(@AuthenticationPrincipal AuthenticatedUser authUser,
                                          @PathVariable String id) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            gameSessionService.leaveGameSession(id, authUser.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/session/{id}")
    public ResponseEntity<?> getSession(@AuthenticationPrincipal AuthenticatedUser authUser,
                                        @PathVariable String id) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            GameSession session = gameSessionService.getGameSession(id);
            // Security check: only players of this session can access
            boolean isPlayerX = session.getPlayerX().getId().equals(authUser.getId());
            boolean isPlayerO = session.getPlayerO() != null && session.getPlayerO().getId().equals(authUser.getId());
            if (!isPlayerX && !isPlayerO && !"WAITING".equals(session.getStatus())) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGameSession(@AuthenticationPrincipal AuthenticatedUser authUser) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            GameSession session = gameSessionService.createGameSession(authUser.getId());
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
