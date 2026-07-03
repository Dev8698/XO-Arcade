package com.tictactoe.service;

import com.tictactoe.entity.Notification;
import java.util.List;

public interface NotificationService {
    Notification createNotification(String type, String senderId, String receiverId, String message);
    Notification createNotification(String type, String senderId, String receiverId, String message, String gameSessionId);
    Notification getNotificationById(Long id);
    List<Notification> getNotificationsForUser(String userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(String userId);
    long getUnreadCount(String userId);
}
