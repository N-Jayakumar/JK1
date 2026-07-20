package com.jk1.dto.response;

import lombok.Data;

@Data
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
}
