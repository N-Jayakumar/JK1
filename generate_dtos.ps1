$dtos = @(
    @{ Name="User"; ReqFields="private String email;`n    private String firstName;`n    private String lastName;`n    private String phone;"; ResFields="private Long id;`n    private String email;`n    private String firstName;`n    private String lastName;" },
    @{ Name="Address"; ReqFields="private String street;`n    private String city;`n    private String state;`n    private String country;`n    private String zipCode;"; ResFields="private Long id;`n    private String street;`n    private String city;`n    private String state;`n    private String country;`n    private String zipCode;" },
    @{ Name="Category"; ReqFields="private String name;`n    private String description;"; ResFields="private Long id;`n    private String name;`n    private String slug;`n    private String description;" },
    @{ Name="Brand"; ReqFields="private String name;`n    private String description;`n    private String logoUrl;"; ResFields="private Long id;`n    private String name;`n    private String description;`n    private String logoUrl;" },
    @{ Name="Product"; ReqFields="private String name;`n    private String sku;`n    private String description;`n    private java.math.BigDecimal price;`n    private Long categoryId;`n    private Long brandId;"; ResFields="private Long id;`n    private String name;`n    private String slug;`n    private String sku;`n    private String description;`n    private java.math.BigDecimal price;" },
    @{ Name="Cart"; ReqFields="private Long userId;"; ResFields="private Long id;`n    private Long userId;`n    private java.math.BigDecimal totalAmount;" },
    @{ Name="Order"; ReqFields="private Long shippingAddressId;`n    private Long couponId;"; ResFields="private Long id;`n    private String orderNumber;`n    private java.math.BigDecimal totalAmount;`n    private String orderStatus;" },
    @{ Name="Review"; ReqFields="private Long productId;`n    private Integer rating;`n    private String comment;"; ResFields="private Long id;`n    private Long productId;`n    private Long userId;`n    private Integer rating;`n    private String comment;" },
    @{ Name="Coupon"; ReqFields="private String code;`n    private Integer discountPercentage;`n    private java.time.LocalDateTime expiryDate;"; ResFields="private Long id;`n    private String code;`n    private Integer discountPercentage;`n    private java.time.LocalDateTime expiryDate;" }
)

New-Item -ItemType Directory -Force -Path "src/main/java/com/jk1/dto/request"
New-Item -ItemType Directory -Force -Path "src/main/java/com/jk1/dto/response"
New-Item -ItemType Directory -Force -Path "src/main/java/com/jk1/mapper"

foreach ($dto in $dtos) {
    $entity = $dto.Name
    $reqDto = "${entity}RequestDTO"
    $resDto = "${entity}ResponseDTO"
    $mapper = "${entity}Mapper"
    
    $reqContent = @"
package com.jk1.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class $reqDto {
    $($dto.ReqFields)
}
"@
    Set-Content -Path "src/main/java/com/jk1/dto/request/${reqDto}.java" -Value $reqContent
    
    $resContent = @"
package com.jk1.dto.response;

import lombok.Data;

@Data
public class $resDto {
    $($dto.ResFields)
}
"@
    Set-Content -Path "src/main/java/com/jk1/dto/response/${resDto}.java" -Value $resContent

    $mapperContent = @"
package com.jk1.mapper;

import com.jk1.dto.request.$reqDto;
import com.jk1.dto.response.$resDto;
import com.jk1.entity.$entity;
import org.springframework.stereotype.Component;

@Component
public class $mapper {

    public $entity toEntity($reqDto dto) {
        if (dto == null) {
            return null;
        }
        $entity entity = new ${entity}();
        // Mapping logic will be expanded here
        return entity;
    }

    public $resDto toResponseDTO($entity entity) {
        if (entity == null) {
            return null;
        }
        $resDto dto = new ${resDto}();
        dto.setId(entity.getId());
        // Mapping logic will be expanded here
        return dto;
    }
}
"@
    Set-Content -Path "src/main/java/com/jk1/mapper/${mapper}.java" -Value $mapperContent
}
Write-Host "DTOs and Mappers successfully generated!"
