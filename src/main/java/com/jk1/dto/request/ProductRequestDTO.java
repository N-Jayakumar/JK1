package com.jk1.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ProductRequestDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String sku;
    private String description;
    @NotNull
    @jakarta.validation.constraints.Positive
    private java.math.BigDecimal price;
    @NotNull
    private Long categoryId;
    @NotNull
    private Long brandId;
}
