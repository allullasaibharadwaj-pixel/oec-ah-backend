package com.example.ecommerce.service;

import com.example.ecommerce.dto.CategoryRequest;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.DuplicateResourceException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll(Sort.by("name"));
    }

    public Category get(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public Category create(CategoryRequest req) {
        String name = req.name().trim();
        if (categoryRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new DuplicateResourceException("A category with this name already exists");
        }
        Category c = new Category();
        c.name = name;
        c.description = req.description();
        return categoryRepository.save(c);
    }

    public Category update(Long id, CategoryRequest req) {
        Category c = get(id);
        String name = req.name().trim();
        categoryRepository.findByNameIgnoreCase(name).ifPresent(other -> {
            if (!other.id.equals(id)) {
                throw new DuplicateResourceException("A category with this name already exists");
            }
        });
        c.name = name;
        c.description = req.description();
        return categoryRepository.save(c);
    }

    public void delete(Long id) {
        Category c = get(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new BadRequestException("This category still has products. Move or delete them first.");
        }
        categoryRepository.delete(c);
    }
}
