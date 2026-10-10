package com.example.ecommerce.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true)
    public User user;
}
