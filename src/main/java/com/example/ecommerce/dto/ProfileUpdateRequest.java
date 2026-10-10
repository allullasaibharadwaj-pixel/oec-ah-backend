package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProfileUpdateRequest(
        @NotBlank(message = "Full name is required") String name,
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Enter a valid phone number (10-15 digits)") String phone,
        @NotBlank(message = "Address is required") String address) {
}
