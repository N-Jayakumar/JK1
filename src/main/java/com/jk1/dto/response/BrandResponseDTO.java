package com.jk1.dto.response;

import lombok.Data;

@Data
public class BrandResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String logoUrl;
}
