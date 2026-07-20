package com.jk1.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CategoryRequestDTO {
    @NotBlank
    private String name;
    private String description;
}
