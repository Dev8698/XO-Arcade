package com.tictactoe.websocket;

import com.tictactoe.dto.MoveRequest;
import com.tictactoe.entity.GameSession;
import com.tictactoe.service.GamePlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    private final GamePlayService gamePlayService;

    @Autowired
    public GameWebSocketController(GamePlayService gamePlayService) {
        this.gamePlayService = gamePlayService;
    }

    @MessageMapping("/game/move")
    public void processMove(MoveRequest request) {
        try {
            gamePlayService.makeMove(
                    request.getGameId(),
                    request.getPlayerId(),
                    request.getCellIndex()
            );
        } catch (Exception e) {
            // Print error, but do not crash websocket mapping
            System.err.println("Error processing game move: " + e.getMessage());
        }
    }
}
