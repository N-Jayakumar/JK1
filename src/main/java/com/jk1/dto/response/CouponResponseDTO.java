package com.jk1.dto.response;

import lombok.Data;

@Data
public class CouponResponseDTO {
    private Long id;
    private String code;
    private Integer discountPercentage;
    private java.math.BigDecimal discountAmount;
    private java.time.LocalDateTime expiryDate;
}
