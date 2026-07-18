package com.saathvik.ecommerce.product.service;

import com.saathvik.ecommerce.product.dto.ProductRequest;
import com.saathvik.ecommerce.product.entity.Product;
import com.saathvik.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .category(request.category())
                .price(request.price())
                .build();
        return productRepository.save(product);
    }

    public Product get(UUID id) {
        return productRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + id));
    }

    public Page<Product> search(String category, String name, Pageable pageable) {
        if (StringUtils.hasText(name)) {
            return productRepository.findByNameContainingIgnoreCaseAndDeletedFalse(name, pageable);
        }
        if (StringUtils.hasText(category)) {
            return productRepository.findByCategoryAndDeletedFalse(category, pageable);
        }
        return productRepository.findByDeletedFalse(pageable);
    }

    public Product update(UUID id, ProductRequest request) {
        Product product = get(id);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategory(request.category());
        product.setPrice(request.price());
        return productRepository.save(product);
    }

    public void softDelete(UUID id) {
        Product product = get(id);
        product.setDeleted(true);
        productRepository.save(product);
    }
}
