package com.example.ecommerce.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    public Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    public Product product;

    /** Product name at the time of purchase. */
    @Column(nullable = false)
    public String productName;

    @Column(nullable = false)
    public int quantity;

    /** Unit price at the time of purchase (future price changes do not affect it). */
    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal price;

    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal subtotal;
}
