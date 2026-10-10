package com.example.ecommerce.dto;

import com.example.ecommerce.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(Long id, Long userId, String userName, String userEmail,
                       BigDecimal subtotal, BigDecimal deliveryCharge, BigDecimal totalAmount,
                       String shippingAddress, String phone, String paymentMethod, String paymentStatus,
                       String orderStatus, LocalDateTime createdAt, List<OrderItemDto> items) {
    public static OrderDto from(Order o) {
        return new OrderDto(o.id, o.user.id, o.user.name, o.user.email,
                o.subtotal, o.deliveryCharge, o.totalAmount,
                o.shippingAddress, o.phone, o.paymentMethod, o.paymentStatus,
                o.orderStatus.name(), o.createdAt,
                o.items.stream().map(OrderItemDto::from).toList());
    }
}
