package com.tictactoe.controller;

import com.tictactoe.entity.User;
import com.tictactoe.security.AuthenticatedUser;
import com.tictactoe.service.FriendService;
import com.tictactoe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;
    private final FriendService friendService;

    @Autowired
    public UserApiController(UserService userService, FriendService friendService) {
        this.userService = userService;
        this.friendService = friendService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal AuthenticatedUser authUser) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserById(authUser.getId());
        
        // Return a response map containing both user and supplementary information (e.g. friendsCount)
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("friendsCount", friendService.getFriends(user.getId()).size());
        
        return ResponseEntity.ok(response);
    }
}
