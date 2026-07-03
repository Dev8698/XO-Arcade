package com.tictactoe.repository;

import com.tictactoe.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiver_IdOrderByCreatedTimeDesc(String receiverId);
    long countByReceiver_IdAndReadStatus(String receiverId, boolean readStatus);
}
