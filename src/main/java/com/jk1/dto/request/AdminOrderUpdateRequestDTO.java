package com.jk1.dto.request;

import com.jk1.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderUpdateRequestDTO {

    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;

    private String trackingNumber;
}
