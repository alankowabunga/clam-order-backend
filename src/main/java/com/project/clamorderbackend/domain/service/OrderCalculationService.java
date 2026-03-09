package com.project.clamorderbackend.domain.service;

import com.project.clamorderbackend.domain.entity.Product;
import com.project.clamorderbackend.domain.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain service for calculating order prices and validating orders.
 * This is the core business logic - pure, testable, with no framework dependencies.
 */
@Service
@RequiredArgsConstructor
public class OrderCalculationService {

    private final ProductService productService;

    /**
     * Calculate price for an order based on items and delivery details
     */
    public PriceCalculation calculate(List<OrderItemRequest> items, 
                                       String deliveryMethod,
                                       String district) {
        
        // Validate inputs
        if (items == null || items.isEmpty()) {
            return PriceCalculation.invalid("請選擇商品");
        }

        // Calculate total weight
        int totalWeight = items.stream()
                .mapToInt(OrderItemRequest::getQty)
                .sum();

        // Determine delivery zone
        DeliveryZone zone = determineZone(deliveryMethod, district);

        // Check minimum weight requirement (skip for pickup)
        if (zone != null && totalWeight < zone.getMinWeight()) {
            String message = getMinWeightMessage(zone, district);
            return PriceCalculation.invalid(message);
        }
        if (totalWeight < zone.getMinWeight()) {
            String message = getMinWeightMessage(zone, district);
            return PriceCalculation.invalid(message);
        }

        // Calculate subtotal
        BigDecimal subtotal = calculateSubtotal(items);

        // Calculate discounts
        boolean isPickup = "PICKUP".equalsIgnoreCase(deliveryMethod);
        BigDecimal bulkDiscount = DiscountPolicy.calculateBulkDiscount(totalWeight);
        BigDecimal pickupDiscount = DiscountPolicy.calculatePickupDiscount(totalWeight, isPickup);
        
        // Calculate shipping fee
        BigDecimal shippingFee = DiscountPolicy.calculateShippingFee(zone, totalWeight);

        // Build success message
        String message = buildSuccessMessage(zone, totalWeight, isPickup);

        return PriceCalculation.valid(totalWeight, subtotal, bulkDiscount, pickupDiscount, shippingFee, message);
    }

    /**
     * Validate and reserve stock for limited products
     */
    public StockValidation validateStock(List<OrderItemRequest> items) {
        List<StockIssue> issues = new ArrayList<>();
        
        for (OrderItemRequest item : items) {
            Product product = productService.findByPublicId(item.getProductId());
            if (product == null) {
                issues.add(new StockIssue(item.getProductId(), "商品不存在"));
                continue;
            }
            
            if (!product.hasStock()) {
                issues.add(new StockIssue(product.getPublicId(), 
                    String.format("「%s」庫存不足", product.getName())));
                continue;
            }
            
            if (product.getIsLimited() && product.getStockRemaining() < item.getQty()) {
                issues.add(new StockIssue(product.getPublicId(),
                    String.format("「%s」庫存不足 (剩餘: %d)", product.getName(), product.getStockRemaining())));
            }
        }
        
        return new StockValidation(issues.isEmpty(), issues);
    }

    /**
     * Reserve stock for order items (atomic operation)
     */
    public boolean reserveStock(List<OrderItemRequest> items) {
        for (OrderItemRequest item : items) {
            Product product = productService.findByPublicId(item.getProductId());
            if (product == null || !product.reserveStock(item.getQty())) {
                return false;
            }
            productService.save(product);
        }
        return true;
    }

    // Helper methods

    private DeliveryZone determineZone(String deliveryMethod, String district) {
        if ("PICKUP".equalsIgnoreCase(deliveryMethod)) {
            return null;
        }
        if ("TAICHUNG_DELIVERY".equalsIgnoreCase(deliveryMethod)) {
            return DeliveryZone.fromDistrict(district);
        }
        return DeliveryZone.valueOf("OTHER_COUNTRIES");
    }

    private BigDecimal calculateSubtotal(List<OrderItemRequest> items) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItemRequest item : items) {
            Product product = productService.findByPublicId(item.getProductId());
            if (product != null) {
                subtotal = subtotal.add(
                    BigDecimal.valueOf(product.getPricePerCatty() * item.getQty())
                );
            }
        }
        return subtotal;
    }

    private String getMinWeightMessage(DeliveryZone zone, String district) {
        if (zone == DeliveryZone.TAICHUNG_SOUTH) {
            return String.format("南區最低訂購量為 %d 斤", zone.getMinWeight());
        }
        return String.format("最低訂購量為 %d 斤", zone.getMinWeight());
    }

    private String buildSuccessMessage(DeliveryZone zone, int totalWeight, boolean isPickup) {
        StringBuilder msg = new StringBuilder();
        
        if (isPickup) {
            msg.append("自取優惠已套用");
        } else if (zone != null && zone.hasFreeShipping()) {
            msg.append("免運優惠已套用");
        } else {
            msg.append("符合配送條件");
        }
        
        String bulkInfo = DiscountPolicy.getBulkDiscountDescription(totalWeight);
        if (!"無".equals(bulkInfo)) {
            msg.append(" / ").append(bulkInfo);
        }
        
        return msg.toString();
    }

    // Inner classes for request/response

    public static class OrderItemRequest {
        private String productId;
        private Integer qty;

        public String getProductId() { return productId; }
        public Integer getQty() { return qty; }
        
        public void setProductId(String productId) { this.productId = productId; }
        public void setQty(Integer qty) { this.qty = qty; }
    }

    public record StockIssue(String productId, String message) {}
    
    public record StockValidation(boolean valid, List<StockIssue> issues) {}
}
