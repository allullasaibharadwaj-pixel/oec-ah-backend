package com.example.ecommerce.dto;

import com.example.ecommerce.entity.CartItem;
import java.math.BigDecimal;

public record CartItemDto(Long id, Long productId, String name, String imageUrl, BigDecimal price,
                          int quantity, int stockQuantity, BigDecimal subtotal) {
    public static CartItemDto from(CartItem ci) {
        return new CartItemDto(ci.id, ci.product.id, ci.product.name, ci.product.imageUrl, ci.product.price,
                ci.quantity, ci.product.stockQuantity, ci.product.price.multiply(BigDecimal.valueOf(ci.quantity)));
    }
}
