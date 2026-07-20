package com.jk1.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private String productImageUrl;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subTotal;
}
