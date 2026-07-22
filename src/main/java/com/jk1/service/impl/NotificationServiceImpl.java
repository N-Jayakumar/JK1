package com.jk1.service.impl;

import com.jk1.entity.Notification;
import com.jk1.entity.NotificationPreference;
import com.jk1.entity.NotificationStatus;
import com.jk1.entity.enums.NotificationType;
import com.jk1.repository.NotificationPreferenceRepository;
import com.jk1.repository.NotificationRepository;
import com.jk1.repository.NotificationStatusRepository;
import com.jk1.repository.UserRepository;
import com.jk1.service.EmailService;
import com.jk1.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationStatusRepository notificationStatusRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

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
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public List<Notification> getUnreadNotifications(String email) {
        com.jk1.entity.User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public long getUnreadCount(String email) {
        com.jk1.entity.User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.countByUserIdAndIsReadFalse(user.getId());
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public void markAllAsRead(String email) {
        com.jk1.entity.User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    @Override
    @Transactional
    public Notification createAndSendNotification(com.jk1.entity.User user, String title, String message, NotificationType type) {
        // 1. Check Preferences
        NotificationPreference pref = notificationPreferenceRepository.findByUser(user)
                .orElseGet(() -> {
                    NotificationPreference defaultPref = NotificationPreference.builder()
                            .user(user)
                            .build();
                    return notificationPreferenceRepository.save(defaultPref);
                });

        boolean shouldSendEmail = shouldSendEmail(pref, type);

        // 2. Create Notification
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .notificationType(type)
                .isRead(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        // 3. Send Email if preferred
        if (shouldSendEmail && pref.isEmailNotificationsEnabled()) {
            boolean success = emailService.sendEmail(user.getEmail(), title, message);
            NotificationStatus status = NotificationStatus.builder()
                    .notification(savedNotification)
                    .isDelivered(success)
                    .deliveryTime(LocalDateTime.now())
                    .errorMessage(success ? null : "Email delivery failed")
                    .build();
            notificationStatusRepository.save(status);
        }

        return savedNotification;
    }

    private boolean shouldSendEmail(NotificationPreference pref, NotificationType type) {
        if (!pref.isEmailNotificationsEnabled()) {
            return false;
        }

        switch (type) {
            case NEW_ORDER:
            case ORDER_CONFIRMED:
            case ORDER_SHIPPED:
            case OUT_FOR_DELIVERY:
            case DELIVERED:
            case CANCELLED:
            case REFUND_APPROVED:
                return pref.isOrderAlertsEnabled();
            case WISHLIST_PRICE_DROP:
                return pref.isWishlistAlertsEnabled();
            case LOW_STOCK_ALERT:
            case NEW_OFFERS:
                return pref.isOfferAlertsEnabled();
            case SYSTEM:
            case EMAIL:
                return true;
            default:
                return false;
        }
    }
}
