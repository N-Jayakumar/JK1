package com.jk1.entity;

import com.jk1.entity.enums.CouponType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseAuditEntity {

    @NotBlank
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Min(0)
    @Max(100)
    @Column(name = "discount_percentage", nullable = false)
    private Integer discountPercentage;

    @PositiveOrZero
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @PositiveOrZero
    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    @PositiveOrZero
    @Column(name = "min_purchase_amount", precision = 10, scale = 2)
    private BigDecimal minPurchaseAmount;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type", nullable = false, length = 50)
    private CouponType couponType;

    @Min(1)
    @Column(name = "usage_limit")
    private Integer usageLimit;

    @OneToMany(mappedBy = "coupon")
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
