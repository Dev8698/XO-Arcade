package com.tictactoe.service;

import com.tictactoe.entity.User;
import com.tictactoe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User syncUser(String id, String email, String username) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isPresent()) {
            User user = existingUserOpt.get();
            boolean updated = false;
            if (!user.getEmail().equalsIgnoreCase(email)) {
                user.setEmail(email);
                updated = true;
            }
            if (username != null && !user.getUsername().equals(username)) {
                user.setUsername(username);
                updated = true;
            }
            // Mark online when syncing/authenticating
            if (!user.isOnline()) {
                user.setOnline(true);
                updated = true;
            }
            if (updated) {
                return userRepository.save(user);
            }
            return user;
        } else {
            // Auto create new user profile linked to Supabase Auth UUID
            User newUser = User.builder()
                    .id(id)
                    .username(username != null ? username : email.split("@")[0])
                    .email(email)
                    .online(true)
                    .build();
            return userRepository.save(newUser);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    @Override
    public void updateOnlineStatus(String id, boolean online) {
        userRepository.findById(id).ifPresent(user -> {
            user.setOnline(online);
            userRepository.save(user);
        });
    }
}
