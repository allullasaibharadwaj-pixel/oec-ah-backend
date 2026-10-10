package com.example.ecommerce.controller;

import com.example.ecommerce.dto.DashboardDto;
import com.example.ecommerce.dto.EnabledRequest;
import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.dto.StatusRequest;
import com.example.ecommerce.dto.UserDto;
import com.example.ecommerce.service.DashboardService;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** All endpoints under /api/admin/** require the ADMIN role (enforced in SecurityConfig). */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final DashboardService dashboardService;
    private final UserService userService;
    private final OrderService orderService;

    public AdminController(DashboardService dashboardService, UserService userService, OrderService orderService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public DashboardDto dashboard() {
        return dashboardService.stats();
    }

    @GetMapping("/users")
    public List<UserDto> users() {
        return userService.listUsers();
    }

    @GetMapping("/users/{id}")
    public UserDto user(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PutMapping("/users/{id}/status")
    public UserDto setEnabled(@PathVariable Long id, @RequestBody EnabledRequest request) {
        return userService.setEnabled(id, request.enabled());
    }

    @GetMapping("/orders")
    public List<OrderDto> orders() {
        return orderService.allOrders();
    }

    @GetMapping("/orders/{id}")
    public OrderDto order(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @PutMapping("/orders/{id}/status")
    public OrderDto updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return orderService.updateStatus(id, request.status());
    }
}
