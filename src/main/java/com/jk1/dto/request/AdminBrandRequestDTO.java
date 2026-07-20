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
public class AdminBrandRequestDTO {

    private Long id;

    @NotBlank(message = "Brand name is required")
    private String name;

    private String description;

    private String logoUrl;
}
