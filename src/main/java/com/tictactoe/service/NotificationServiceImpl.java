package com.tictactoe.service;

import com.tictactoe.entity.Notification;
import com.tictactoe.entity.User;
import com.tictactoe.repository.NotificationRepository;
import com.tictactoe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public Notification createNotification(String type, String senderId, String receiverId, String message) {
        return createNotification(type, senderId, receiverId, message, null);
    }

    @Override
    public Notification createNotification(String type, String senderId, String receiverId, String message, String gameSessionId) {
        User sender = null;
        if (senderId != null) {
            sender = userRepository.findById(senderId).orElse(null);
        }
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found with ID: " + receiverId));

        Notification notification = Notification.builder()
                .type(type)
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .gameSessionId(gameSessionId)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        try {
            messagingTemplate.convertAndSendToUser(receiverId, "/queue/notifications", savedNotification);
        } catch (Exception e) {
            // Suppress WebSocket errors during integration tests or when user is offline
            System.err.println("Realtime push failed: " + e.getMessage());
        }

        return savedNotification;
    }

    @Override
    @Transactional(readOnly = true)
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(String userId) {
        return notificationRepository.findByReceiver_IdOrderByCreatedTimeDesc(userId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setReadStatus(true);
            notificationRepository.save(n);
        });
    }

    @Override
    public void markAllAsRead(String userId) {
        List<Notification> notifications = notificationRepository.findByReceiver_IdOrderByCreatedTimeDesc(userId);
        notifications.forEach(n -> {
            if (!n.isReadStatus()) {
                n.setReadStatus(true);
            }
        });
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String userId) {
        return notificationRepository.countByReceiver_IdAndReadStatus(userId, false);
    }
}
