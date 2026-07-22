package com.jk1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationStatus extends BaseAuditEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Column(name = "is_delivered", nullable = false)
    @Builder.Default
    private boolean isDelivered = false;

    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    @Column(name = "error_message", length = 500)
    private String errorMessage;
}
