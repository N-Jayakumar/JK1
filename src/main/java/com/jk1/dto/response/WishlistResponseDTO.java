package com.jk1.dto.response;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
public class WishlistResponseDTO {
    private Long id;
    private Long userId;
    private List<ProductResponseDTO> products = new ArrayList<>();
}
