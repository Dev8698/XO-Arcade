package com.tictactoe.controller;

import com.tictactoe.entity.FriendRequest;
import com.tictactoe.entity.Notification;
import com.tictactoe.entity.User;
import com.tictactoe.repository.FriendRequestRepository;
import com.tictactoe.security.AuthenticatedUser;
import com.tictactoe.service.FriendService;
import com.tictactoe.service.GameSessionService;
import com.tictactoe.service.NotificationService;
import com.tictactoe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendApiController {

    private final FriendService friendService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final FriendRequestRepository friendRequestRepository;
    private final GameSessionService gameSessionService;

    @Autowired
    public FriendApiController(FriendService friendService,
                               UserService userService,
                               NotificationService notificationService,
                               FriendRequestRepository friendRequestRepository,
                               GameSessionService gameSessionService) {
        this.friendService = friendService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.friendRequestRepository = friendRequestRepository;
        this.gameSessionService = gameSessionService;
    }

    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveFriends(@AuthenticationPrincipal AuthenticatedUser authUser) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        List<User> friends = friendService.getFriends(authUser.getId());
        // Sort: Online players first, then alphabetically by username
        friends.sort((u1, u2) -> {
            if (u1.isOnline() != u2.isOnline()) {
                return Boolean.compare(u2.isOnline(), u1.isOnline());
            }
            return u1.getUsername().compareToIgnoreCase(u2.getUsername());
        });
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@AuthenticationPrincipal AuthenticatedUser authUser,
                                                  @RequestParam String query) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        List<User> users = userService.searchUsers(query);
        // Exclude the current user from search list
        users.removeIf(u -> u.getId().equals(authUser.getId()));
        return ResponseEntity.ok(users);
    }

    @PostMapping("/request/send/{receiverId}")
    public ResponseEntity<String> sendFriendRequest(@AuthenticationPrincipal AuthenticatedUser authUser,
                                                    @PathVariable String receiverId) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            friendService.sendFriendRequest(authUser.getId(), receiverId);
            return ResponseEntity.ok("Friend request successfully sent.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/request/{notificationId}/accept")
    public ResponseEntity<Void> acceptRequest(@AuthenticationPrincipal AuthenticatedUser authUser,
                                              @PathVariable Long notificationId) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        Notification notif = notificationService.getNotificationById(notificationId);
        if (!notif.getReceiver().getId().equals(authUser.getId())) {
            return ResponseEntity.status(403).build();
        }
        FriendRequest req = friendRequestRepository.findBySender_IdAndReceiver_Id(
            notif.getSender().getId(), notif.getReceiver().getId()
        ).orElseThrow(() -> new RuntimeException("Friend request not found"));

        friendService.acceptFriendRequest(req.getId());
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request/{notificationId}/reject")
    public ResponseEntity<Void> rejectRequest(@AuthenticationPrincipal AuthenticatedUser authUser,
                                              @PathVariable Long notificationId) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        Notification notif = notificationService.getNotificationById(notificationId);
        if (!notif.getReceiver().getId().equals(authUser.getId())) {
            return ResponseEntity.status(403).build();
        }
        FriendRequest req = friendRequestRepository.findBySender_IdAndReceiver_Id(
            notif.getSender().getId(), notif.getReceiver().getId()
        ).orElseThrow(() -> new RuntimeException("Friend request not found"));

        friendService.rejectFriendRequest(req.getId());
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request/direct/{requestId}/accept")
    public ResponseEntity<Void> acceptRequestDirect(@AuthenticationPrincipal AuthenticatedUser authUser,
                                                    @PathVariable Long requestId) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        friendService.acceptFriendRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request/direct/{requestId}/reject")
    public ResponseEntity<Void> rejectRequestDirect(@AuthenticationPrincipal AuthenticatedUser authUser,
                                                    @PathVariable Long requestId) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        friendService.rejectFriendRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{friendId}")
    public ResponseEntity<Void> removeFriend(@AuthenticationPrincipal AuthenticatedUser authUser,
                                             @PathVariable String friendId) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        friendService.removeFriend(authUser.getId(), friendId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/challenge/{friendId}")
    public ResponseEntity<Void> challengeFriend(@AuthenticationPrincipal AuthenticatedUser authUser,
                                                @PathVariable String friendId) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            gameSessionService.challengeFriend(authUser.getId(), friendId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FriendRequest>> getPendingRequests(@AuthenticationPrincipal AuthenticatedUser authUser) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        List<FriendRequest> requests = friendService.getPendingRequestsReceived(authUser.getId());
        return ResponseEntity.ok(requests);
    }
}
