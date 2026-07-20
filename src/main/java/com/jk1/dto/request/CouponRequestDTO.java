package com.jk1.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CouponRequestDTO {
    @NotBlank
    private String code;
    @NotNull
    @jakarta.validation.constraints.Positive
    private Integer discountPercentage;
    @jakarta.validation.constraints.PositiveOrZero
    private java.math.BigDecimal discountAmount;
    @NotNull
    @jakarta.validation.constraints.Future
    private java.time.LocalDateTime expiryDate;
}
