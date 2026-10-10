package com.example.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardDto(long totalProducts, long totalUsers, long totalOrders, BigDecimal totalRevenue,
                           long pendingOrders, long deliveredOrders, Map<String, Long> statusSummary,
                           List<OrderDto> recentOrders, List<ProductDto> recentProducts) {
}
