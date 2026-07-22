package com.jk1.service;

import com.jk1.entity.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationService {
    Notification save(Notification notification);
    Optional<Notification> findById(Long id);
    List<Notification> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Notification> getUserNotifications(String email);
    List<Notification> getUnreadNotifications(String email);
    long getUnreadCount(String email);
    void markAsRead(Long id, String email);
    void markAllAsRead(String email);
    Notification createAndSendNotification(com.jk1.entity.User user, String title, String message, com.jk1.entity.enums.NotificationType type);
}
