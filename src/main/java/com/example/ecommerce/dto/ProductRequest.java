package com.example.ecommerce.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Product name is required") String name,
        String description,
        @NotNull(message = "Price is required") @DecimalMin(value = "0.01", message = "Price must be greater than 0") BigDecimal price,
        @DecimalMin(value = "0.01", message = "Old price must be greater than 0") BigDecimal oldPrice,
        @NotNull(message = "Category is required") Long categoryId,
        String brand,
        String imageUrl,
        @NotNull(message = "Stock quantity is required") @Min(value = 0, message = "Stock cannot be negative") Integer stockQuantity,
        @DecimalMin(value = "0", message = "Rating must be between 0 and 5") @DecimalMax(value = "5", message = "Rating must be between 0 and 5") Double rating) {
}
