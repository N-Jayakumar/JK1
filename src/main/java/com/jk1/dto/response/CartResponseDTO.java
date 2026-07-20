package com.jk1.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Data
public class CartResponseDTO {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private List<CartItemDTO> items = new ArrayList<>();
}
