package com.tictactoe.controller;

import com.tictactoe.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal AuthenticatedUser authUser) {
        if (authUser != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/signup")
    public String signup(@AuthenticationPrincipal AuthenticatedUser authUser) {
        if (authUser != null) {
            return "redirect:/dashboard";
        }
        return "signup";
    }
}
