package com.jk1.mapper;

import com.jk1.dto.request.CartRequestDTO;
import com.jk1.dto.response.CartResponseDTO;
import com.jk1.entity.Cart;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {

    public Cart toEntity(CartRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Cart entity = new Cart();
        // Mapping logic will be expanded here
        return entity;
    }

    public CartResponseDTO toResponseDTO(Cart entity) {
        if (entity == null) {
            return null;
        }
        CartResponseDTO dto = new CartResponseDTO();
        dto.setId(entity.getId());
        // Mapping logic will be expanded here
        return dto;
    }
}
