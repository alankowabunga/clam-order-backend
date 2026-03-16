package com.project.clamorderbackend.application.usecase;

import com.project.clamorderbackend.application.dto.*;
import com.project.clamorderbackend.application.mapper.OrderMapper;
import com.project.clamorderbackend.domain.entity.*;
import com.project.clamorderbackend.domain.repository.*;
import com.project.clamorderbackend.domain.service.*;
import com.project.clamorderbackend.domain.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case for order operations
 */
@Service
@RequiredArgsConstructor
public class OrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderCalculationService calculationService;
    private final OrderMapper orderMapper;

    /**
     * Convert string to DeliveryMethod enum
     */
    private Order.DeliveryMethod parseDeliveryMethod(String value) {
        if (value == null) {
            throw new IllegalArgumentException("配送方式不能為空");
        }
        String normalized = value.toUpperCase().replace("-", "_");
        try {
            return Order.DeliveryMethod.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("無效的配送方式: " + value + ". 支援: PICKUP, TAICHUNG_DELIVERY, HOME_DELIVERY");
        }
    }

    /**
     * Calculate order price
     */
    public OrderCalculateResponse calculatePrice(OrderCalculateRequest request) {
        // Convert DTO to domain requests
        List<OrderCalculationService.OrderItemRequest> items = request.getItems().stream()
                .map(dto -> {
                    OrderCalculationService.OrderItemRequest item = 
                        new OrderCalculationService.OrderItemRequest();
                    item.setProductId(dto.getProductId());
                    item.setQty(dto.getQty());
                    return item;
                })
                .collect(Collectors.toList());

        // Calculate price
        PriceCalculation calculation = calculationService.calculate(
                items,
                request.getDeliveryMethod(),
                request.getDistrict()
        );

        return orderMapper.toCalculateResponse(calculation);
    }

    /**
     * Submit a new order
     */
    @Transactional
    public OrderSubmitResponse submitOrder(OrderSubmitRequest request) {
        // Convert DTO to domain requests
        List<OrderCalculationService.OrderItemRequest> items = request.getItems().stream()
                .map(dto -> {
                    OrderCalculationService.OrderItemRequest item = 
                        new OrderCalculationService.OrderItemRequest();
                    item.setProductId(dto.getProductId());
                    item.setQty(dto.getQty());
                    return item;
                })
                .collect(Collectors.toList());

        // First, validate stock
        var stockValidation = calculationService.validateStock(items);
        if (!stockValidation.valid()) {
            throw new IllegalArgumentException(
                    stockValidation.issues().stream()
                            .map(issue -> issue.message())
                            .collect(Collectors.joining(", "))
            );
        }

        // Calculate price
        PriceCalculation calculation = calculationService.calculate(
                items,
                request.getDeliveryMethod(),
                request.getDistrict()
        );

        if (!calculation.getIsValid()) {
            throw new IllegalArgumentException(calculation.getMessage());
        }

        // Reserve stock
        if (!calculationService.reserveStock(items)) {
            throw new IllegalStateException("庫存鎖定失敗，請重新下單");
        }

        // Create order
        Order order = Order.builder()
                .customerName(request.getCustomerName())
                .phone(request.getPhone())
                .deliveryMethod(parseDeliveryMethod(request.getDeliveryMethod()))
                .address(request.getAddress())
                .district(request.getDistrict())
                .status(Order.OrderStatus.PENDING_PAYMENT)
                .totalWeight(calculation.getTotalWeight())
                .subtotal(calculation.getSubtotal())
                .bulkDiscount(calculation.getBulkDiscount())
                .pickupDiscount(calculation.getPickupDiscount())
                .shippingFee(calculation.getShippingFee())
                .totalPay(calculation.getFinalAmount())
                .isManagementOfficeCollect(request.getIsManagementOfficeCollect())
                .paymentLastFive(request.getPaymentLastFive())
                .notes(request.getNotes())
                .build();

        // Add order items
        for (OrderSubmitRequest.OrderItemDto itemDto : request.getItems()) {
            Product product = productRepository.findByPublicId(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("商品不存在: " + itemDto.getProductId()));

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .quantity(itemDto.getQty())
                    .unitPrice(product.getPricePerCatty())
                    .subtotal(product.getPricePerCatty() * itemDto.getQty())
                    .build();
            order.addItem(orderItem);
        }

        // Save order
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toSubmitResponse(savedOrder);
    }

    /**
     * Export orders
     */
    public List<OrderExportResponse> exportOrders(String groupBy) {
        List<Order> orders;

        if (groupBy != null && "district".equalsIgnoreCase(groupBy)) {
            // Group by district - get all orders
            orders = orderRepository.findAllOrderByCreatedAtDesc();
        } else {
            orders = orderRepository.findAllOrderByCreatedAtDesc();
        }

        return orders.stream()
                .map(orderMapper::toExportResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all orders (for admin)
     */
    public List<OrderExportResponse> getAllOrders() {
        return orderRepository.findAllOrderByCreatedAtDesc().stream()
                .map(orderMapper::toExportResponse)
                .collect(Collectors.toList());
    }

    public OrderExportResponse toExportResponse(Order order) {
        return orderMapper.toExportResponse(order);
    }
}
