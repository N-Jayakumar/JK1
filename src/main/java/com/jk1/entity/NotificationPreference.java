package com.jk1.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference extends BaseAuditEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "email_notifications", nullable = false)
    @Builder.Default
    private boolean emailNotificationsEnabled = true;

    @Column(name = "order_alerts", nullable = false)
    @Builder.Default
    private boolean orderAlertsEnabled = true;

    @Column(name = "offer_alerts", nullable = false)
    @Builder.Default
    private boolean offerAlertsEnabled = true;

    @Column(name = "wishlist_alerts", nullable = false)
    @Builder.Default
    private boolean wishlistAlertsEnabled = true;
}
