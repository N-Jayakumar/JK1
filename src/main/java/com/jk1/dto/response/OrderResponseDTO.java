package com.jk1.dto.response;

import lombok.Data;

@Data
public class OrderResponseDTO {
    private Long id;
    private String orderNumber;
    private java.math.BigDecimal totalAmount;
    private String orderStatus;
}
