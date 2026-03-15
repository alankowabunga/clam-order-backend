package com.project.clamorderbackend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Product entity - represents a clam product available for order.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", unique = true, nullable = false)
    private String publicId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price_per_catty", nullable = false)
    private Integer pricePerCatty;

    @Column(name = "description")
    private String description;

    @Column(name = "is_limited", nullable = false)
    @Builder.Default
    private Boolean isLimited = false;

    @Column(name = "stock_remaining")
    private Integer stockRemaining;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (publicId == null) {
            publicId = "p" + id;
        }
    }

    /**
     * Check if product has available stock (for limited products)
     */
    public boolean hasStock() {
        if (!isLimited) {
            return true; // Non-limited products always available
        }
        return stockRemaining != null && stockRemaining > 0;
    }

    /**
     * Reserve stock for an order (for limited products)
     */
    public boolean reserveStock(Integer quantity) {
        if (!isLimited) {
            return true;
        }
        if (stockRemaining == null || stockRemaining < quantity) {
            return false;
        }
        stockRemaining -= quantity;
        return true;
    }

    public Integer getStockRemaining() {
        return stockRemaining != null ? stockRemaining : 0;
    }
}
