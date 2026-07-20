package com.jk1.mapper;

import com.jk1.dto.request.ReviewRequestDTO;
import com.jk1.dto.response.ReviewResponseDTO;
import com.jk1.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public Review toEntity(ReviewRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Review entity = new Review();
        // Mapping logic will be expanded here
        return entity;
    }

    public ReviewResponseDTO toResponseDTO(Review entity) {
        if (entity == null) {
            return null;
        }
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(entity.getId());
        // Mapping logic will be expanded here
        return dto;
    }
}
