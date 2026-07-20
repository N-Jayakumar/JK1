package com.jk1.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCategoryRequestDTO {
    
    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    private Long parentId;
}
