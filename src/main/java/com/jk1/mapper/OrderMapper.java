package com.jk1.mapper;

import com.jk1.dto.request.OrderRequestDTO;
import com.jk1.dto.response.OrderResponseDTO;
import com.jk1.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public Order toEntity(OrderRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Order entity = new Order();
        // Mapping logic will be expanded here
        return entity;
    }

    public OrderResponseDTO toResponseDTO(Order entity) {
        if (entity == null) {
            return null;
        }
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(entity.getId());
        // Mapping logic will be expanded here
        return dto;
    }
}
