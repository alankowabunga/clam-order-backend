package com.project.clamorderbackend.domain.service;

import com.project.clamorderbackend.domain.entity.Product;
import com.project.clamorderbackend.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Domain service for Product operations.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Find all active products
     */
    public List<Product> findAllActive() {
        return productRepository.findByIsActiveTrue();
    }

    /**
     * Find product by public ID
     */
    public Product findByPublicId(String publicId) {
        return productRepository.findByPublicId(publicId).orElse(null);
    }

    /**
     * Save product (used for stock updates)
     */
    public Product save(Product product) {
        return productRepository.save(product);
    }

    /**
     * Get product map by public ID for quick lookup
     */
    public java.util.Map<String, Product> getProductMap() {
        List<Product> products = findAllActive();
        return products.stream()
                .collect(java.util.stream.Collectors.toMap(
                        Product::getPublicId,
                        p -> p
                ));
    }
}
