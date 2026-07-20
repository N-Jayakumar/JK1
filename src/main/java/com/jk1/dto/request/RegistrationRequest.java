package com.jk1.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO bound from the HTML registration form (POST /register).
 * The form submits a single "fullName" field which is split
 * into firstName / lastName in the controller.
 */
@Data
public class RegistrationRequest {

    /** Combined full name from the form — split on first space in the controller. */
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    /** Optional phone number — no constraint enforced here. */
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;
}
