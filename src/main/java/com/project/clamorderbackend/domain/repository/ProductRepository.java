package com.project.clamorderbackend.domain.repository;

import com.project.clamorderbackend.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all active products
     */
    List<Product> findByIsActiveTrue();

    /**
     * Find product by public ID
     */
    Optional<Product> findByPublicId(String publicId);
}
