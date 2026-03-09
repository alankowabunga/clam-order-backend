package com.project.clamorderbackend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity - represents a customer order.
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_public_id", columnList = "public_id"),
    @Index(name = "idx_orders_status", columnList = "status"),
    @Index(name = "idx_orders_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", unique = true, nullable = false)
    private String publicId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method", nullable = false)
    private DeliveryMethod deliveryMethod;

    @Column(name = "address")
    private String address;

    @Column(name = "district")
    private String district;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    @Column(name = "total_weight", nullable = false)
    private Integer totalWeight;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    @Column(name = "bulk_discount")
    private BigDecimal bulkDiscount;

    @Column(name = "pickup_discount")
    private BigDecimal pickupDiscount;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Column(name = "total_pay", nullable = false)
    private BigDecimal totalPay;

    @Column(name = "is_management_office_collect")
    private Boolean isManagementOfficeCollect;

    @Column(name = "payment_last_five")
    private String paymentLastFive;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (publicId == null) {
            publicId = generatePublicId();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generatePublicId() {
        return "ORD-" + LocalDateTime.now().toString().substring(0, 10).replace("-", "") 
               + "-" + String.format("%03d", (int)(Math.random() * 1000));
    }

    /**
     * Add an item to the order
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * Calculate total weight from items
     */
    public void recalculateTotalWeight() {
        this.totalWeight = items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    // Enum for delivery methods
    public enum DeliveryMethod {
        PICKUP,              // 自取
        TAICHUNG_DELIVERY,   // 台中配送
        HOME_DELIVERY        // 宅配送貨
    }

    // Enum for order status
    public enum OrderStatus {
        PENDING_PAYMENT,     // 待核帳
        PAID,                // 已付款
        READY_TO_SHIP,       // 待出貨
        COMPLETED,           // 已完結
        CANCELLED           // 已取消
    }
}
