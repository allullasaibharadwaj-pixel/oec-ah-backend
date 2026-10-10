package com.example.ecommerce.service;

import com.example.ecommerce.dto.CheckoutRequest;
import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        CartItemRepository cartItemRepository, CartService cartService, UserService userService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
        this.userService = userService;
    }

    public OrderDto placeOrder(String email, CheckoutRequest req) {
        User user = userService.getByEmail(email);
        Cart cart = cartService.getOrCreateCart(user);
        List<CartItem> cartItems = cartItemRepository.findByCartOrderByIdAsc(cart);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Your cart is empty");
        }

        Order order = new Order();
        order.user = user;
        order.shippingAddress = req.shippingAddress().trim();
        order.phone = req.phone().trim();
        order.paymentMethod = req.paymentMethod();
        order.paymentStatus = "CARD".equals(req.paymentMethod()) ? "PAID" : "PENDING";
        order.orderStatus = OrderStatus.PLACED;

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            Product product = productRepository.findByIdForUpdate(ci.product.id)
                    .orElseThrow(() -> new BadRequestException("A product in your cart no longer exists"));
            if (!product.active) {
                throw new BadRequestException(product.name + " is no longer available. Please remove it from your cart.");
            }
            if (product.stockQuantity < ci.quantity) {
                throw new InsufficientStockException("Insufficient stock for " + product.name
                        + ". Available: " + product.stockQuantity);
            }
            product.stockQuantity -= ci.quantity;
            product.salesCount += ci.quantity;

            OrderItem item = new OrderItem();
            item.order = order;
            item.product = product;
            item.productName = product.name;
            item.quantity = ci.quantity;
            item.price = product.price;
            item.subtotal = product.price.multiply(BigDecimal.valueOf(ci.quantity));
            order.items.add(item);
            subtotal = subtotal.add(item.subtotal);
        }
        order.subtotal = subtotal;
        order.deliveryCharge = Pricing.deliveryCharge(subtotal);
        order.totalAmount = subtotal.add(order.deliveryCharge);

        Order saved = orderRepository.save(order);
        cartItemRepository.deleteByCart(cart);
        return OrderDto.from(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> myOrders(String email) {
        User user = userService.getByEmail(email);
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream().map(OrderDto::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderDto myOrder(String email, Long id) {
        Order order = find(id);
        if (!order.user.email.equals(email)) {
            // never reveal that another user's order exists
            throw new ResourceNotFoundException("Order not found");
        }
        return OrderDto.from(order);
    }

    public OrderDto cancelMyOrder(String email, Long id) {
        Order order = find(id);
        if (!order.user.email.equals(email)) {
            throw new ResourceNotFoundException("Order not found");
        }
        if (order.orderStatus != OrderStatus.PLACED && order.orderStatus != OrderStatus.CONFIRMED) {
            throw new BadRequestException("This order can no longer be cancelled");
        }
        cancel(order);
        return OrderDto.from(orderRepository.save(order));
    }

    // ----- admin -----
    @Transactional(readOnly = true)
    public List<OrderDto> allOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream().map(OrderDto::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderDto getOrder(Long id) {
        return OrderDto.from(find(id));
    }

    public OrderDto updateStatus(Long id, String statusText) {
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(statusText.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + statusText);
        }
        Order order = find(id);
        if (order.orderStatus == OrderStatus.CANCELLED || order.orderStatus == OrderStatus.DELIVERED) {
            throw new BadRequestException("A " + order.orderStatus.name().toLowerCase()
                    + " order can no longer be changed");
        }
        if (status == OrderStatus.CANCELLED) {
            cancel(order);
        } else {
            order.orderStatus = status;
            if (status == OrderStatus.DELIVERED && "PENDING".equals(order.paymentStatus)) {
                order.paymentStatus = "PAID"; // cash collected on delivery
            }
        }
        return OrderDto.from(orderRepository.save(order));
    }

    private void cancel(Order order) {
        for (OrderItem item : order.items) {
            Product product = productRepository.findByIdForUpdate(item.product.id).orElse(null);
            if (product != null) {
                product.stockQuantity += item.quantity;
                product.salesCount = Math.max(0, product.salesCount - item.quantity);
            }
        }
        order.orderStatus = OrderStatus.CANCELLED;
        if ("PAID".equals(order.paymentStatus)) {
            order.paymentStatus = "REFUNDED";
        }
    }

    private Order find(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
}
