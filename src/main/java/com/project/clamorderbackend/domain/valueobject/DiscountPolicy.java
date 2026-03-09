package com.project.clamorderbackend.domain.valueobject;

import lombok.Getter;
import java.math.BigDecimal;

/**
 * Value object representing discount policies.
 */
public class DiscountPolicy {

    private DiscountPolicy() {}

    // Bulk discount rules: weight threshold -> discount per catty
    public static final int BULK_DISCOUNT_TIER_1_THRESHOLD = 10;
    public static final int BULK_DISCOUNT_TIER_2_THRESHOLD = 20;
    public static final BigDecimal BULK_DISCOUNT_TIER_1 = BigDecimal.valueOf(5);  // -5/jin
    public static final BigDecimal BULK_DISCOUNT_TIER_2 = BigDecimal.valueOf(10); // -10/jin

    // Pickup discount: -10/jin
    public static final BigDecimal PICKUP_DISCOUNT = BigDecimal.valueOf(10);

    // Shipping fee for non-free zones
    public static final BigDecimal SHIPPING_FEE_STANDARD = BigDecimal.valueOf(250);
    public static final int SHIPPING_FEE_FREE_THRESHOLD = 15;  // 15+ jin = free

    /**
     * Calculate bulk discount based on total weight
     */
    public static BigDecimal calculateBulkDiscount(Integer totalWeight) {
        if (totalWeight == null || totalWeight < BULK_DISCOUNT_TIER_1_THRESHOLD) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discountPerCatty;
        if (totalWeight >= BULK_DISCOUNT_TIER_2_THRESHOLD) {
            discountPerCatty = BULK_DISCOUNT_TIER_2;
        } else {
            discountPerCatty = BULK_DISCOUNT_TIER_1;
        }
        
        // Discount is negative (reduces total)
        return discountPerCatty.multiply(BigDecimal.valueOf(totalWeight)).negate();
    }

    /**
     * Calculate pickup discount
     */
    public static BigDecimal calculatePickupDiscount(Integer totalWeight, boolean isPickup) {
        if (!isPickup || totalWeight == null) {
            return BigDecimal.ZERO;
        }
        // Discount is negative (reduces total)
        return PICKUP_DISCOUNT.multiply(BigDecimal.valueOf(totalWeight)).negate();
    }

    /**
     * Calculate shipping fee based on zone and weight
     */
    public static BigDecimal calculateShippingFee(DeliveryZone zone, Integer totalWeight) {
        // Pickup has no shipping fee
        if (zone == null) {
            return BigDecimal.ZERO;
        }
        
        if (totalWeight == null) {
            return SHIPPING_FEE_STANDARD;
        }
        if (zone == null || totalWeight == null) {
            return SHIPPING_FEE_STANDARD;
        }

        // Free shipping zones
        if (zone.hasFreeShipping() && totalWeight >= zone.getMinWeight()) {
            return BigDecimal.ZERO;
        }

        // Non-free zones (OTHER_COUNTRIES)
        if (totalWeight >= SHIPPING_FEE_FREE_THRESHOLD) {
            return BigDecimal.ZERO;
        }

        return SHIPPING_FEE_STANDARD;
    }

    /**
     * Get the applicable bulk discount tier description
     */
    public static String getBulkDiscountDescription(Integer totalWeight) {
        if (totalWeight == null || totalWeight < BULK_DISCOUNT_TIER_1_THRESHOLD) {
            return "無";
        }
        if (totalWeight >= BULK_DISCOUNT_TIER_2_THRESHOLD) {
            return String.format("滿%d斤每斤-$%d", totalWeight, BULK_DISCOUNT_TIER_2.intValue());
        }
        return String.format("滿%d斤每斤-$%d", BULK_DISCOUNT_TIER_1_THRESHOLD, BULK_DISCOUNT_TIER_1.intValue());
    }
}
