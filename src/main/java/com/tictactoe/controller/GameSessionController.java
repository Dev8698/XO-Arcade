package com.tictactoe.controller;

import com.tictactoe.entity.GameSession;
import com.tictactoe.entity.User;
import com.tictactoe.security.AuthenticatedUser;
import com.tictactoe.service.GameSessionService;
import com.tictactoe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/game")
public class GameSessionController {

    private final GameSessionService gameSessionService;
    private final UserService userService;

    @Autowired
    public GameSessionController(GameSessionService gameSessionService, UserService userService) {
        this.gameSessionService = gameSessionService;
        this.userService = userService;
    }

    // --- MVC Page Mappings ---

    @GetMapping("/create")
    public String createGamePage(@AuthenticationPrincipal AuthenticatedUser authUser, Model model) {
        if (authUser == null) {
            return "redirect:/login";
        }
        User user = userService.getUserById(authUser.getId());
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("pageTitle", "Create Session - Tic Tac Toe");
        return "create-game";
    }

    @PostMapping("/create")
    public String createGame(@AuthenticationPrincipal AuthenticatedUser authUser) {
        if (authUser == null) {
            return "redirect:/login";
        }
        GameSession session = gameSessionService.createGameSession(authUser.getId());
        return "redirect:/game/waiting/" + session.getId();
    }

    @GetMapping("/waiting/{id}")
    public String waitingRoom(@AuthenticationPrincipal AuthenticatedUser authUser,
                              @PathVariable String id,
                              Model model) {
        if (authUser == null) {
            return "redirect:/login";
        }
        GameSession session = gameSessionService.getGameSession(id);
        
        // Security check: Only player X can stay in waiting room, unless player O is already joined
        if (!session.getPlayerX().getId().equals(authUser.getId()) && 
            (session.getPlayerO() == null || !session.getPlayerO().getId().equals(authUser.getId()))) {
            return "redirect:/dashboard";
        }

        // If another player already joined, redirect directly to the game board
        if ("PLAYING".equals(session.getStatus())) {
            return "redirect:/game/board/" + session.getId();
        }

        User user = userService.getUserById(authUser.getId());
        model.addAttribute("currentUser", user);
        model.addAttribute("gameSession", session);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("pageTitle", "Waiting Room - Tic Tac Toe");
        return "waiting-room";
    }

    @GetMapping("/board/{id}")
    public String gameBoard(@AuthenticationPrincipal AuthenticatedUser authUser,
                            @PathVariable String id,
                            Model model) {
        if (authUser == null) {
            return "redirect:/login";
        }
        GameSession session = gameSessionService.getGameSession(id);

        // Security check: only players of this session can access the board
        boolean isPlayerX = session.getPlayerX().getId().equals(authUser.getId());
        boolean isPlayerO = session.getPlayerO() != null && session.getPlayerO().getId().equals(authUser.getId());
        
        if (!isPlayerX && !isPlayerO) {
            return "redirect:/dashboard";
        }

        User user = userService.getUserById(authUser.getId());
        model.addAttribute("currentUser", user);
        model.addAttribute("gameSession", session);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("pageTitle", "Tic Tac Toe Arena");
        return "game-board";
    }

    @GetMapping("/join")
    public String joinPage(@AuthenticationPrincipal AuthenticatedUser authUser, Model model) {
        if (authUser == null) {
            return "redirect:/login";
        }
        User user = userService.getUserById(authUser.getId());
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("pageTitle", "Join Arena - Tic Tac Toe");
        return "join-game";
    }
}
