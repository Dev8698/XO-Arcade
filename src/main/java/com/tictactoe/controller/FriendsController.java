package com.tictactoe.controller;

import com.tictactoe.entity.FriendRequest;
import com.tictactoe.entity.User;
import com.tictactoe.security.AuthenticatedUser;
import com.tictactoe.service.FriendService;
import com.tictactoe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class FriendsController {

    private final FriendService friendService;
    private final UserService userService;

    @Autowired
    public FriendsController(FriendService friendService, UserService userService) {
        this.friendService = friendService;
        this.userService = userService;
    }

    @GetMapping("/friends")
    public String friendsPage(@AuthenticationPrincipal AuthenticatedUser authUser, Model model) {
        if (authUser == null) {
            return "redirect:/login";
        }
        User user = userService.getUserById(authUser.getId());
        model.addAttribute("currentUser", user);

        // Retrieve active friends
        List<User> friends = friendService.getFriends(authUser.getId());
        friends.sort((u1, u2) -> {
            if (u1.isOnline() != u2.isOnline()) {
                return Boolean.compare(u2.isOnline(), u1.isOnline());
            }
            return u1.getUsername().compareToIgnoreCase(u2.getUsername());
        });
        model.addAttribute("friends", friends);
        model.addAttribute("friendsCount", friends.size());

        // Retrieve pending requests
        List<FriendRequest> pendingRequests = friendService.getPendingRequestsReceived(authUser.getId());
        model.addAttribute("pendingRequests", pendingRequests);

        model.addAttribute("activePage", "friends");
        model.addAttribute("pageTitle", "Friends Management - Tic Tac Toe");
        return "friends";
    }
}
