package com.example.ecommerce.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    public User user;

    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal deliveryCharge;

    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal totalAmount;

    @Column(nullable = false, length = 500)
    public String shippingAddress;

    @Column(nullable = false)
    public String phone;

    /** COD or CARD */
    @Column(nullable = false)
    public String paymentMethod;

    /** PENDING, PAID or REFUNDED */
    @Column(nullable = false)
    public String paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public OrderStatus orderStatus = OrderStatus.PLACED;

    @Column(nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<OrderItem> items = new ArrayList<>();
}
