package com.jk1.dto.request;

import com.jk1.entity.enums.CouponType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCouponRequestDTO {

    private Long id;

    @NotBlank(message = "Coupon code is required")
    private String code;

    @NotNull(message = "Coupon type is required")
    private CouponType couponType;

    @Min(value = 0, message = "Discount percentage cannot be negative")
    @Max(value = 100, message = "Discount percentage cannot exceed 100")
    private Integer discountPercentage;

    @PositiveOrZero(message = "Discount amount cannot be negative")
    private BigDecimal discountAmount;

    @PositiveOrZero(message = "Maximum discount amount cannot be negative")
    private BigDecimal maxDiscountAmount;

    @PositiveOrZero(message = "Minimum purchase amount cannot be negative")
    private BigDecimal minPurchaseAmount;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime expiryDate;

    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer usageLimit;

    @Builder.Default
    private boolean isActive = true;
}
