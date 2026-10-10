package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CheckoutRequest(
        @NotBlank(message = "Shipping address is required") String shippingAddress,
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Enter a valid phone number (10-15 digits)") String phone,
        @NotBlank(message = "Payment method is required")
        @Pattern(regexp = "COD|CARD", message = "Payment method must be COD or CARD") String paymentMethod) {
}
