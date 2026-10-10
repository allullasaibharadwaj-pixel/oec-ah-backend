package com.example.ecommerce.service;

import com.example.ecommerce.dto.DashboardDto;
import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public DashboardService(ProductRepository productRepository, UserRepository userRepository,
                            OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public DashboardDto stats() {
        BigDecimal revenue = orderRepository.findByOrderStatusNot(OrderStatus.CANCELLED).stream()
                .map((Order o) -> o.totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> summary = new LinkedHashMap<>();
        for (OrderStatus s : OrderStatus.values()) {
            summary.put(s.name(), orderRepository.countByOrderStatus(s));
        }
        long pending = summary.get("PLACED") + summary.get("CONFIRMED") + summary.get("PACKED") + summary.get("SHIPPED");

        List<OrderDto> recentOrders = orderRepository.findTop5ByOrderByCreatedAtDesc().stream().map(OrderDto::from).toList();
        List<ProductDto> recentProducts = productRepository.findTop5ByActiveTrueOrderByCreatedAtDesc().stream()
                .map(ProductDto::from).toList();

        return new DashboardDto(productRepository.countByActiveTrue(), userRepository.count(), orderRepository.count(),
                revenue, pending, summary.get("DELIVERED"), summary, recentOrders, recentProducts);
    }
}
