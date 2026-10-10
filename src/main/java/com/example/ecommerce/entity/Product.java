package com.example.ecommerce.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String name;

    @Column(length = 2000)
    public String description;

    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal price;

    /** Original price before discount (optional). Used to display a SALE badge. */
    @Column(precision = 10, scale = 2)
    public BigDecimal oldPrice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    public Category category;

    public String brand;

    @Column(length = 1000)
    public String imageUrl;

    @Column(nullable = false)
    public int stockQuantity;

    @Column(nullable = false)
    public double rating;

    @Column(nullable = false)
    public int salesCount;

    /** false = soft deleted (product that already appears in past orders). */
    @Column(nullable = false)
    public boolean active = true;

    @Column(nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
}
