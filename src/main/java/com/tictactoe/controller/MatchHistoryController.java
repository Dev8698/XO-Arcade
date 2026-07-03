package com.tictactoe.controller;

import com.tictactoe.entity.MatchHistory;
import com.tictactoe.entity.User;
import com.tictactoe.security.AuthenticatedUser;
import com.tictactoe.service.MatchHistoryService;
import com.tictactoe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MatchHistoryController {

    private final MatchHistoryService matchHistoryService;
    private final UserService userService;

    @Autowired
    public MatchHistoryController(MatchHistoryService matchHistoryService, UserService userService) {
        this.matchHistoryService = matchHistoryService;
        this.userService = userService;
    }

    @GetMapping("/history")
    public String matchHistory(@AuthenticationPrincipal AuthenticatedUser authUser,
                               @RequestParam(required = false, defaultValue = "ALL") String filter,
                               Model model) {
        if (authUser == null) {
            return "redirect:/login";
        }
        User user = userService.getUserById(authUser.getId());
        List<MatchHistory> history = matchHistoryService.getMatchHistoryFiltered(authUser.getId(), filter);

        model.addAttribute("currentUser", user);
        model.addAttribute("historyList", history);
        model.addAttribute("selectedFilter", filter.toUpperCase());
        model.addAttribute("activePage", "history");
        model.addAttribute("pageTitle", "Match History - Tic Tac Toe");
        return "history";
    }
}
