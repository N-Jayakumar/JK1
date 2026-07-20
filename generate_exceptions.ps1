New-Item -ItemType Directory -Force -Path "src/main/java/com/jk1/exception"
New-Item -ItemType Directory -Force -Path "src/main/java/com/jk1/validation/annotations"
New-Item -ItemType Directory -Force -Path "src/main/java/com/jk1/validation/validator"

Set-Content -Path "src/main/java/com/jk1/exception/ResourceNotFoundException.java" -Value @"
package com.jk1.exception;
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
"@

Set-Content -Path "src/main/java/com/jk1/exception/DuplicateResourceException.java" -Value @"
package com.jk1.exception;
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) { super(message); }
}
"@

Set-Content -Path "src/main/java/com/jk1/exception/InvalidOperationException.java" -Value @"
package com.jk1.exception;
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) { super(message); }
}
"@

$specificNotFounds = @("User", "Product", "Category", "Order", "Cart")
foreach ($nf in $specificNotFounds) {
    Set-Content -Path "src/main/java/com/jk1/exception/${nf}NotFoundException.java" -Value @"
package com.jk1.exception;
public class ${nf}NotFoundException extends ResourceNotFoundException {
    public ${nf}NotFoundException(String message) { super(message); }
}
"@
}

Set-Content -Path "src/main/java/com/jk1/dto/response/ErrorResponseDTO.java" -Value @"
package com.jk1.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponseDTO {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
"@

Set-Content -Path "src/main/java/com/jk1/exception/GlobalExceptionHandler.java" -Value @"
package com.jk1.exception;

import com.jk1.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateResourceException(DuplicateResourceException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidOperationException(InvalidOperationException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errors, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }
}
"@

$dtos = @(
    @{ Name="User"; ReqFields="    @NotBlank(message = `"Email is required`")`n    @jakarta.validation.constraints.Email(message = `"Invalid email`")`n    private String email;`n    @NotBlank(message = `"First name is required`")`n    private String firstName;`n    @NotBlank(message = `"Last name is required`")`n    private String lastName;`n    private String phone;" },
    @{ Name="Address"; ReqFields="    @NotBlank`n    private String street;`n    @NotBlank`n    private String city;`n    @NotBlank`n    private String state;`n    @NotBlank`n    private String country;`n    @NotBlank`n    private String zipCode;" },
    @{ Name="Category"; ReqFields="    @NotBlank`n    private String name;`n    private String description;" },
    @{ Name="Brand"; ReqFields="    @NotBlank`n    private String name;`n    private String description;`n    private String logoUrl;" },
    @{ Name="Product"; ReqFields="    @NotBlank`n    private String name;`n    @NotBlank`n    private String sku;`n    private String description;`n    @NotNull`n    @jakarta.validation.constraints.Positive`n    private java.math.BigDecimal price;`n    @NotNull`n    private Long categoryId;`n    @NotNull`n    private Long brandId;" },
    @{ Name="Cart"; ReqFields="    @NotNull`n    private Long userId;" },
    @{ Name="Order"; ReqFields="    @NotNull`n    private Long shippingAddressId;`n    private Long couponId;" },
    @{ Name="Review"; ReqFields="    @NotNull`n    private Long productId;`n    @NotNull`n    @jakarta.validation.constraints.Min(1)`n    @jakarta.validation.constraints.Max(5)`n    private Integer rating;`n    @NotBlank`n    private String comment;" },
    @{ Name="Coupon"; ReqFields="    @NotBlank`n    private String code;`n    @NotNull`n    @jakarta.validation.constraints.Positive`n    private Integer discountPercentage;`n    @NotNull`n    @jakarta.validation.constraints.Future`n    private java.time.LocalDateTime expiryDate;" }
)

foreach ($dto in $dtos) {
    $entity = $dto.Name
    $reqDto = "${entity}RequestDTO"
    
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
}
Write-Host "Exceptions and Validation successfully generated!"
