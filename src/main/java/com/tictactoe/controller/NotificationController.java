package com.tictactoe.controller;

import com.tictactoe.entity.Notification;
import com.tictactoe.entity.User;
import com.tictactoe.security.AuthenticatedUser;
import com.tictactoe.service.NotificationService;
import com.tictactoe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/notifications")
    public String notifications(@AuthenticationPrincipal AuthenticatedUser authUser, Model model) {
        if (authUser == null) {
            return "redirect:/login";
        }
        User user = userService.getUserById(authUser.getId());
        List<Notification> notifications = notificationService.getNotificationsForUser(authUser.getId());

        model.addAttribute("currentUser", user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("activePage", "notifications");
        model.addAttribute("pageTitle", "Notifications - Tic Tac Toe");
        return "notifications";
    }

    // --- REST Endpoints for AJAX and WebSocket Refresh ---

    @GetMapping("/api/notifications")
    @ResponseBody
    public ResponseEntity<List<Notification>> getUserNotifications(@AuthenticationPrincipal AuthenticatedUser authUser) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        List<Notification> notifications = notificationService.getNotificationsForUser(authUser.getId());
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/api/notifications/{id}/read")
    @ResponseBody
    public ResponseEntity<Void> markRead(@AuthenticationPrincipal AuthenticatedUser authUser,
                                         @PathVariable Long id) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        // Verification of ownership is done in the service or simplified here
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/notifications/read-all")
    @ResponseBody
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal AuthenticatedUser authUser) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        notificationService.markAllAsRead(authUser.getId());
        return ResponseEntity.ok().build();
    }
}
