package com.jk1.mapper;

import com.jk1.dto.request.UserRequestDTO;
import com.jk1.dto.response.UserResponseDTO;
import com.jk1.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        User entity = new User();
        // Mapping logic will be expanded here
        return entity;
    }

    public UserResponseDTO toResponseDTO(User entity) {
        if (entity == null) {
            return null;
        }
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(entity.getId());
        // Mapping logic will be expanded here
        return dto;
    }
}
