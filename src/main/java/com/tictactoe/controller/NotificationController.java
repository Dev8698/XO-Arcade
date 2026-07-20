package com.tictactoe.controller;

import com.tictactoe.entity.Notification;
import com.tictactoe.security.AuthenticatedUser;
import com.tictactoe.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // --- REST Endpoints for AJAX and WebSocket Refresh ---

    @GetMapping("/api/notifications")
    public ResponseEntity<List<Notification>> getUserNotifications(@AuthenticationPrincipal AuthenticatedUser authUser) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        List<Notification> notifications = notificationService.getNotificationsForUser(authUser.getId());
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/api/notifications/{id}/read")
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
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal AuthenticatedUser authUser) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        notificationService.markAllAsRead(authUser.getId());
        return ResponseEntity.ok().build();
    }
}
