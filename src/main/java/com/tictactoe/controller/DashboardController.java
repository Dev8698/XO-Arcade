package com.tictactoe.controller;

import com.tictactoe.entity.User;
import com.tictactoe.security.AuthenticatedUser;
import com.tictactoe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserService userService;

    @Autowired
    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal AuthenticatedUser authUser, Model model) {
        if (authUser == null) {
            return "redirect:/login";
        }
        User user = userService.getUserById(authUser.getId());
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("pageTitle", "Dashboard - Tic Tac Toe");
        return "dashboard";
    }
}
