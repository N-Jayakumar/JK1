package com.jk1.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class UserRequestDTO {
    @NotBlank(message = "Email is required")
    @jakarta.validation.constraints.Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    private String phone;
}
