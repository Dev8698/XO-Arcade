package com.tictactoe.service;

import com.tictactoe.entity.Friend;
import com.tictactoe.entity.FriendRequest;
import com.tictactoe.entity.User;
import com.tictactoe.repository.FriendRepository;
import com.tictactoe.repository.FriendRequestRepository;
import com.tictactoe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public FriendServiceImpl(FriendRepository friendRepository,
                             FriendRequestRepository friendRequestRepository,
                             UserRepository userRepository,
                             NotificationService notificationService) {
        this.friendRepository = friendRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void sendFriendRequest(String senderId, String receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("You cannot send a friend request to yourself.");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found."));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found."));

        // Check if already friends
        if (friendRepository.existsByUser_IdAndFriend_Id(senderId, receiverId)) {
            throw new IllegalStateException("You are already friends with this user.");
        }

        // Check if pending request exists
        if (friendRequestRepository.existsBySender_IdAndReceiver_IdAndStatus(senderId, receiverId, "PENDING") ||
            friendRequestRepository.existsBySender_IdAndReceiver_IdAndStatus(receiverId, senderId, "PENDING")) {
            throw new IllegalStateException("A pending friend request already exists between you.");
        }

        // Create new request
        FriendRequest request = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status("PENDING")
                .build();
        friendRequestRepository.save(request);

        // Send Notification
        notificationService.createNotification(
                "FRIEND_REQUEST", 
                senderId, 
                receiverId, 
                sender.getUsername() + " sent you a friend request."
        );
    }

    @Override
    public void acceptFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found."));

        if (!"PENDING".equals(request.getStatus())) {
            throw new IllegalStateException("This request is not pending.");
        }

        User sender = request.getSender();
        User receiver = request.getReceiver();

        // Save mutual friendships
        Friend f1 = Friend.builder().user(sender).friend(receiver).build();
        Friend f2 = Friend.builder().user(receiver).friend(sender).build();
        friendRepository.save(f1);
        friendRepository.save(f2);

        // Delete the request
        friendRequestRepository.delete(request);

        // Send notification
        notificationService.createNotification(
                "FRIEND_ACCEPT", 
                receiver.getId(), 
                sender.getId(), 
                receiver.getUsername() + " accepted your friend request!"
        );
    }

    @Override
    public void rejectFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found."));
        
        request.setStatus("REJECTED");
        friendRequestRepository.save(request);
        
        // Delete request to keep database clean
        friendRequestRepository.delete(request);
    }

    @Override
    public void cancelFriendRequest(String senderId, String receiverId) {
        friendRequestRepository.findBySender_IdAndReceiver_Id(senderId, receiverId)
                .ifPresent(friendRequestRepository::delete);
    }

    @Override
    public void removeFriend(String userId, String friendId) {
        friendRepository.findByUser_IdAndFriend_Id(userId, friendId).ifPresent(friendRepository::delete);
        friendRepository.findByUser_IdAndFriend_Id(friendId, userId).ifPresent(friendRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getFriends(String userId) {
        return friendRepository.findByUser_Id(userId)
                .stream()
                .map(Friend::getFriend)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getOnlineFriends(String userId) {
        return getFriends(userId)
                .stream()
                .filter(User::isOnline)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRequest> getPendingRequestsReceived(String userId) {
        return friendRequestRepository.findByReceiver_IdAndStatus(userId, "PENDING");
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRequest> getPendingRequestsSent(String userId) {
        return friendRequestRepository.findBySender_IdAndStatus(userId, "PENDING");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFriend(String userId, String friendId) {
        return friendRepository.existsByUser_IdAndFriend_Id(userId, friendId);
    }
}
