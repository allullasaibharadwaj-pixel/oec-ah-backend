package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.dto.CartItemDto;
import com.example.ecommerce.dto.CartRequest;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       UserService userService, ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.productService = productService;
    }

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.user = user;
            return cartRepository.save(cart);
        });
    }

    public CartDto getCart(String email) {
        return toDto(getOrCreateCart(userService.getByEmail(email)));
    }

    public CartDto add(String email, CartRequest req) {
        Cart cart = getOrCreateCart(userService.getByEmail(email));
        Product product = productService.findActive(req.productId());
        CartItem item = cartItemRepository.findByCartAndProduct(cart, product).orElse(null);
        int newQuantity = (item == null ? 0 : item.quantity) + req.quantity();
        checkStock(product, newQuantity);
        if (item == null) {
            item = new CartItem();
            item.cart = cart;
            item.product = product;
        }
        item.quantity = newQuantity;
        cartItemRepository.save(item);
        return toDto(cart);
    }

    public CartDto update(String email, CartRequest req) {
        Cart cart = getOrCreateCart(userService.getByEmail(email));
        Product product = productService.findActive(req.productId());
        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new BadRequestException("This product is not in your cart"));
        checkStock(product, req.quantity());
        item.quantity = req.quantity();
        cartItemRepository.save(item);
        return toDto(cart);
    }

    public CartDto remove(String email, Long productId) {
        Cart cart = getOrCreateCart(userService.getByEmail(email));
        Product product = productService.findActive(productId);
        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new BadRequestException("This product is not in your cart"));
        cartItemRepository.delete(item);
        return toDto(cart);
    }

    public CartDto clear(String email) {
        Cart cart = getOrCreateCart(userService.getByEmail(email));
        cartItemRepository.deleteByCart(cart);
        return toDto(cart);
    }

    private void checkStock(Product product, int quantity) {
        if (quantity < 1) {
            throw new BadRequestException("Quantity must be at least 1");
        }
        if (product.stockQuantity < 1) {
            throw new InsufficientStockException(product.name + " is out of stock");
        }
        if (quantity > product.stockQuantity) {
            throw new InsufficientStockException("Only " + product.stockQuantity + " units of "
                    + product.name + " are available");
        }
    }

    private CartDto toDto(Cart cart) {
        List<CartItemDto> items = cartItemRepository.findByCartOrderByIdAsc(cart)
                .stream().map(CartItemDto::from).toList();
        int count = items.stream().mapToInt(CartItemDto::quantity).sum();
        BigDecimal subtotal = items.stream().map(CartItemDto::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal delivery = Pricing.deliveryCharge(subtotal);
        return new CartDto(items, count, subtotal, delivery, subtotal.add(delivery));
    }
}
