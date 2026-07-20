package com.jk1.mapper;

import com.jk1.dto.request.CouponRequestDTO;
import com.jk1.dto.response.CouponResponseDTO;
import com.jk1.entity.Coupon;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {

    public Coupon toEntity(CouponRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Coupon entity = new Coupon();
        entity.setCode(dto.getCode());
        entity.setDiscountPercentage(dto.getDiscountPercentage());
        entity.setDiscountAmount(dto.getDiscountAmount());
        entity.setExpiryDate(dto.getExpiryDate());
        return entity;
    }

    public CouponResponseDTO toResponseDTO(Coupon entity) {
        if (entity == null) {
            return null;
        }
        CouponResponseDTO dto = new CouponResponseDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setDiscountPercentage(entity.getDiscountPercentage());
        dto.setDiscountAmount(entity.getDiscountAmount());
        dto.setExpiryDate(entity.getExpiryDate());
        return dto;
    }
}
