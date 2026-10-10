package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.dto.CartRequest;
import com.example.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartDto get(Authentication auth) {
        return cartService.getCart(auth.getName());
    }

    @PostMapping("/add")
    public CartDto add(Authentication auth, @Valid @RequestBody CartRequest request) {
        return cartService.add(auth.getName(), request);
    }

    @PutMapping("/update")
    public CartDto update(Authentication auth, @Valid @RequestBody CartRequest request) {
        return cartService.update(auth.getName(), request);
    }

    /** {id} is the product id. */
    @DeleteMapping("/remove/{id}")
    public CartDto remove(Authentication auth, @PathVariable Long id) {
        return cartService.remove(auth.getName(), id);
    }

    @DeleteMapping("/clear")
    public CartDto clear(Authentication auth) {
        return cartService.clear(auth.getName());
    }
}
