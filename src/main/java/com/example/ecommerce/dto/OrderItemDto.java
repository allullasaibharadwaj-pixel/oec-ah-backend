package com.example.ecommerce.dto;

import com.example.ecommerce.entity.OrderItem;
import java.math.BigDecimal;

public record OrderItemDto(Long id, Long productId, String productName, String imageUrl,
                           int quantity, BigDecimal price, BigDecimal subtotal) {
    public static OrderItemDto from(OrderItem i) {
        return new OrderItemDto(i.id, i.product.id, i.productName, i.product.imageUrl,
                i.quantity, i.price, i.subtotal);
    }
}
