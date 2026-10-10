package com.example.ecommerce.dto;

import com.example.ecommerce.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductDto(Long id, String name, String description, BigDecimal price, BigDecimal oldPrice,
                         Long categoryId, String categoryName, String brand, String imageUrl,
                         int stockQuantity, double rating, int salesCount, LocalDateTime createdAt) {
    public static ProductDto from(Product p) {
        return new ProductDto(p.id, p.name, p.description, p.price, p.oldPrice,
                p.category.id, p.category.name, p.brand, p.imageUrl,
                p.stockQuantity, p.rating, p.salesCount, p.createdAt);
    }
}
