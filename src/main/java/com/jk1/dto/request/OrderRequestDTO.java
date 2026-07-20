package com.jk1.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class OrderRequestDTO {
    @NotNull
    private Long shippingAddressId;
    private Long couponId;
    @NotBlank
    private String paymentMethod;
}
