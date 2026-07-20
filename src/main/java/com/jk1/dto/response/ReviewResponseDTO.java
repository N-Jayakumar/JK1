package com.jk1.dto.response;

import lombok.Data;

@Data
public class ReviewResponseDTO {
    private Long id;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String comment;
}
