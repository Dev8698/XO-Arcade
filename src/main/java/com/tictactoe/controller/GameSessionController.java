package com.tictactoe.controller;

import com.tictactoe.entity.GameSession;
import com.tictactoe.security.AuthenticatedUser;
import com.tictactoe.service.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/game")
public class GameSessionController {

    private final GameSessionService gameSessionService;

    @Autowired
    public GameSessionController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @GetMapping("/create")
    public String showCreateForm() {
        return "forward:/static/create-game.html";
    }

    @PostMapping("/create")
    public String createGame(@AuthenticationPrincipal AuthenticatedUser authUser) {
        if (authUser == null) {
            return "redirect:/login";
        }
        GameSession session = gameSessionService.createGameSession(authUser.getId());
        return "redirect:/game/waiting/" + session.getId();
    }
}
