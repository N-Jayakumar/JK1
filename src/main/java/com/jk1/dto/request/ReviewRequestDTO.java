package com.jk1.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ReviewRequestDTO {
    @NotNull
    private Long productId;
    @NotNull
    @jakarta.validation.constraints.Min(1)
    @jakarta.validation.constraints.Max(5)
    private Integer rating;
    @NotBlank
    private String comment;
}
