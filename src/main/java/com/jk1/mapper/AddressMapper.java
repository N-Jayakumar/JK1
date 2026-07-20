package com.jk1.mapper;

import com.jk1.dto.request.AddressRequestDTO;
import com.jk1.dto.response.AddressResponseDTO;
import com.jk1.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toEntity(AddressRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Address entity = new Address();
        // Mapping logic will be expanded here
        return entity;
    }

    public AddressResponseDTO toResponseDTO(Address entity) {
        if (entity == null) {
            return null;
        }
        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setId(entity.getId());
        // Mapping logic will be expanded here
        return dto;
    }
}
