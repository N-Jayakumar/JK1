package com.jk1.service.impl;

import com.jk1.entity.Notification;
import com.jk1.repository.NotificationRepository;
import com.jk1.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.jk1.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }

    @Override
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return notificationRepository.existsById(id);
    }

    @Override
    public List<Notification> getUserNotifications(String email) {
        com.jk1.entity.User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserId(user.getId());
    }

    @Override
    public List<Notification> getUnreadNotifications(String email) {
        com.jk1.entity.User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserIdAndIsReadFalse(user.getId());
    }

    @Override
    public void markAsRead(Long id, String email) {
        com.jk1.entity.User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Notification notif = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!notif.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        notif.setRead(true);
        notificationRepository.save(notif);
    }
}
