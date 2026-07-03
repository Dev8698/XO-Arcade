package com.tictactoe.repository;

import com.tictactoe.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    Optional<FriendRequest> findBySender_IdAndReceiver_Id(String senderId, String receiverId);
    List<FriendRequest> findByReceiver_IdAndStatus(String receiverId, String status);
    List<FriendRequest> findBySender_IdAndStatus(String senderId, String status);
    boolean existsBySender_IdAndReceiver_Id(String senderId, String receiverId);
    
    // Check if there is an active pending request in either direction
    boolean existsBySender_IdAndReceiver_IdAndStatus(String senderId, String receiverId, String status);
}
