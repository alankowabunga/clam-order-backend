package com.project.clamorderbackend.application.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for order calculation response
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCalculateResponse {

    private Integer totalWeight;
    private BigDecimal subtotal;
    private BigDecimal bulkDiscount;
    private BigDecimal pickupDiscount;
    private BigDecimal shippingFee;
    private BigDecimal finalAmount;
    private Boolean isValid;
    private String message;
}
