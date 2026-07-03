package com.tictactoe.service;

import com.tictactoe.entity.User;
import java.util.List;

public interface UserService {
    User syncUser(String id, String email, String username);
    User getUserById(String id);
    User getUserByEmail(String email);
    List<User> searchUsers(String query);
    void updateOnlineStatus(String id, boolean online);
}
