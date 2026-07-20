package com.jk1.dto.response;

import lombok.Data;

@Data
public class AddressResponseDTO {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String addressType;
    private boolean isDefault;
}
