package com.project.clamorderbackend.domain.valueobject;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

/**
 * Value object representing the result of a price calculation.
 */
@Getter
@Builder
public class PriceCalculation {

    private final Integer totalWeight;
    private final BigDecimal subtotal;
    private final BigDecimal bulkDiscount;
    private final BigDecimal pickupDiscount;
    private final BigDecimal shippingFee;
    private final BigDecimal finalAmount;
    private final Boolean isValid;
    private final String message;

    /**
     * Factory method to create a valid calculation result
     */
    public static PriceCalculation valid(Integer totalWeight, BigDecimal subtotal, 
                                         BigDecimal bulkDiscount, BigDecimal pickupDiscount,
                                         BigDecimal shippingFee, String message) {
        BigDecimal finalAmount = subtotal
                .add(bulkDiscount != null ? bulkDiscount : BigDecimal.ZERO)
                .add(pickupDiscount != null ? pickupDiscount : BigDecimal.ZERO)
                .add(shippingFee != null ? shippingFee : BigDecimal.ZERO);
        
        return PriceCalculation.builder()
                .totalWeight(totalWeight)
                .subtotal(subtotal)
                .bulkDiscount(bulkDiscount)
                .pickupDiscount(pickupDiscount)
                .shippingFee(shippingFee)
                .finalAmount(finalAmount)
                .isValid(true)
                .message(message)
                .build();
    }

    /**
     * Factory method to create an invalid calculation result
     */
    public static PriceCalculation invalid(String message) {
        return PriceCalculation.builder()
                .isValid(false)
                .message(message)
                .build();
    }
}
