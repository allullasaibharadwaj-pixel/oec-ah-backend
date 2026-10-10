package com.example.ecommerce.service;

import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.dto.ProductRequest;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
                          CartItemRepository cartItemRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductDto> search(String search, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                   String sort, Integer limit) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.<Boolean>get("active")));
            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim().toLowerCase() + "%";
                Join<Product, Category> category = root.join("category", JoinType.LEFT);
                predicates.add(cb.or(
                        cb.like(cb.lower(root.<String>get("name")), like),
                        cb.like(cb.lower(root.<String>get("brand")), like),
                        cb.like(cb.lower(root.<String>get("description")), like),
                        cb.like(cb.lower(category.<String>get("name")), like)));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("price"), maxPrice));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort order = switch (sort == null ? "newest" : sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "rating" -> Sort.by(Sort.Direction.DESC, "rating").and(Sort.by(Sort.Direction.DESC, "createdAt"));
            case "popular" -> Sort.by(Sort.Direction.DESC, "salesCount").and(Sort.by(Sort.Direction.DESC, "createdAt"));
            default -> Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id"));
        };

        List<Product> products = (limit != null && limit > 0)
                ? productRepository.findAll(spec, PageRequest.of(0, limit, order)).getContent()
                : productRepository.findAll(spec, order);
        return products.stream().map(ProductDto::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductDto get(Long id) {
        return ProductDto.from(findActive(id));
    }

    public Product findActive(Long id) {
        return productRepository.findById(id)
                .filter(p -> p.active)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public ProductDto create(ProductRequest req) {
        Product p = new Product();
        apply(p, req);
        return ProductDto.from(productRepository.save(p));
    }

    public ProductDto update(Long id, ProductRequest req) {
        Product p = findActive(id);
        apply(p, req);
        return ProductDto.from(productRepository.save(p));
    }

    /** Hard delete when never ordered; otherwise hide it (soft delete) so past orders stay intact. */
    public void delete(Long id) {
        Product p = findActive(id);
        cartItemRepository.deleteByProductId(id);
        if (orderItemRepository.existsByProductId(id)) {
            p.active = false;
            productRepository.save(p);
        } else {
            productRepository.delete(p);
        }
    }

    private void apply(Product p, ProductRequest req) {
        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        p.name = req.name().trim();
        p.description = req.description();
        p.price = req.price();
        p.oldPrice = req.oldPrice();
        p.category = category;
        p.brand = req.brand();
        p.imageUrl = (req.imageUrl() == null || req.imageUrl().isBlank()) ? "images/placeholder.svg" : req.imageUrl().trim();
        p.stockQuantity = req.stockQuantity();
        p.rating = req.rating() == null ? 0 : req.rating();
    }
}
