package com.jk1.entity;

import com.jk1.entity.enums.RefundStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "refund_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequest extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotBlank
    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false, length = 50)
    @Builder.Default
    private RefundStatus refundStatus = RefundStatus.PENDING;

    @Column(name = "admin_note", length = 500)
    private String adminNote;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;
}
