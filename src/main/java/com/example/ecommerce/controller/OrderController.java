package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CheckoutRequest;
import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> place(Authentication auth, @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(auth.getName(), request));
    }

    @GetMapping
    public List<OrderDto> mine(Authentication auth) {
        return orderService.myOrders(auth.getName());
    }

    @GetMapping("/{id}")
    public OrderDto get(Authentication auth, @PathVariable Long id) {
        return orderService.myOrder(auth.getName(), id);
    }

    @PutMapping("/{id}/cancel")
    public OrderDto cancel(Authentication auth, @PathVariable Long id) {
        return orderService.cancelMyOrder(auth.getName(), id);
    }
}
