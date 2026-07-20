package com.jk1.mapper;

import com.jk1.dto.request.CategoryRequestDTO;
import com.jk1.dto.response.CategoryResponseDTO;
import com.jk1.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Category entity = new Category();
        // Mapping logic will be expanded here
        return entity;
    }

    public CategoryResponseDTO toResponseDTO(Category entity) {
        if (entity == null) {
            return null;
        }
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setDescription(entity.getDescription());
        
        // Mock image url for now until we have image entity for category
        dto.setImageUrl("https://images.unsplash.com/photo-1617137968427-85924c800a22?q=80&w=600&auto=format&fit=crop");
        
        return dto;
    }
}
