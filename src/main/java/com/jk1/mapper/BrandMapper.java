package com.jk1.mapper;

import com.jk1.dto.request.BrandRequestDTO;
import com.jk1.dto.response.BrandResponseDTO;
import com.jk1.entity.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {

    public Brand toEntity(BrandRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Brand entity = new Brand();
        // Mapping logic will be expanded here
        return entity;
    }

    public BrandResponseDTO toResponseDTO(Brand entity) {
        if (entity == null) {
            return null;
        }
        BrandResponseDTO dto = new BrandResponseDTO();
        dto.setId(entity.getId());
        // Mapping logic will be expanded here
        return dto;
    }
}
