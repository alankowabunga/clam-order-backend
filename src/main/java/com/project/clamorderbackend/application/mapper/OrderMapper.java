package com.project.clamorderbackend.application.mapper;

import com.project.clamorderbackend.application.dto.*;
import com.project.clamorderbackend.domain.entity.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for converting between entities and DTOs
 */
@Component
public class OrderMapper {

    /**
     * Convert Product entity to ProductResponse DTO
     */
    public ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getPublicId())
                .name(product.getName())
                .pricePerCatty(product.getPricePerCatty())
                .description(product.getDescription())
                .isLimited(product.getIsLimited())
                .stockRemaining(product.getStockRemaining())
                .build();
    }

    /**
     * Convert PriceCalculation to OrderCalculateResponse
     */
    public OrderCalculateResponse toCalculateResponse(
            com.project.clamorderbackend.domain.valueobject.PriceCalculation calculation) {
        return OrderCalculateResponse.builder()
                .totalWeight(calculation.getTotalWeight())
                .subtotal(calculation.getSubtotal())
                .bulkDiscount(calculation.getBulkDiscount())
                .pickupDiscount(calculation.getPickupDiscount())
                .shippingFee(calculation.getShippingFee())
                .finalAmount(calculation.getFinalAmount())
                .isValid(calculation.getIsValid())
                .message(calculation.getMessage())
                .build();
    }

    /**
     * Convert Order to OrderExportResponse
     */
    public OrderExportResponse toExportResponse(Order order) {
        String itemsSummary = order.getItems().stream()
                .map(item -> String.format("%s x%d斤", 
                        item.getProductName(), 
                        item.getQuantity()))
                .collect(Collectors.joining(", "));

        return OrderExportResponse.builder()
                .orderId(order.getPublicId())
                .customerName(order.getCustomerName())
                .phone(order.getPhone())
                .deliveryMethod(order.getDeliveryMethod().name())
                .address(order.getAddress())
                .district(order.getDistrict())
                .totalWeight(order.getTotalWeight())
                .items(itemsSummary)
                .isManagementOfficeCollect(order.getIsManagementOfficeCollect())
                .paymentLastFive(order.getPaymentLastFive())
                .status(order.getStatus().name())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt().toString())
                .build();
    }

    /**
     * Convert Order to OrderSubmitResponse
     */
    public OrderSubmitResponse toSubmitResponse(Order order) {
        return OrderSubmitResponse.builder()
                .orderId(order.getPublicId())
                .status(order.getStatus().name())
                .totalPay(order.getTotalPay().toString())
                .message("訂單已收到，請於指定時間前完成付款")
                .build();
    }
}
