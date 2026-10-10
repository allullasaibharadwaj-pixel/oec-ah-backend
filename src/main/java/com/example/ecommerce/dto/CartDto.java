package com.example.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(List<CartItemDto> items, int itemCount, BigDecimal subtotal,
                      BigDecimal deliveryCharge, BigDecimal total) {
}
