package com.tictactoe.service;

import com.tictactoe.entity.FriendRequest;
import com.tictactoe.entity.User;
import java.util.List;

public interface FriendService {
    void sendFriendRequest(String senderId, String receiverId);
    void acceptFriendRequest(Long requestId);
    void rejectFriendRequest(Long requestId);
    void cancelFriendRequest(String senderId, String receiverId);
    void removeFriend(String userId, String friendId);
    List<User> getFriends(String userId);
    List<User> getOnlineFriends(String userId);
    List<FriendRequest> getPendingRequestsReceived(String userId);
    List<FriendRequest> getPendingRequestsSent(String userId);
    boolean isFriend(String userId, String friendId);
}
