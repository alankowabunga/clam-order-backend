package com.project.clamorderbackend.presentation.controller;

import com.project.clamorderbackend.application.dto.*;
import com.project.clamorderbackend.application.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for admin operations
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderUseCase orderUseCase;

    /**
     * Get all orders
     */
    @GetMapping("/orders")
    public ResponseEntity<List<OrderExportResponse>> getAllOrders() {
        return ResponseEntity.ok(orderUseCase.getAllOrders());
    }

    /**
     * Export orders (CSV format)
     */
    @GetMapping("/orders/export")
    public ResponseEntity<byte[]> exportOrders(
            @RequestParam(required = false) String groupBy) {
        
        List<OrderExportResponse> orders = orderUseCase.exportOrders(groupBy);
        
        // Generate CSV
        StringBuilder csv = new StringBuilder();
        csv.append("訂單編號,姓名,電話,配送方式,地址,地區,重量,項目,管理室代收,付款末五碼,狀態,備註,建立時間\n");
        
        for (OrderExportResponse order : orders) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%d,%s,%s,%s,%s,%s,%s\n",
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getPhone(),
                    order.getDeliveryMethod(),
                    order.getAddress() != null ? order.getAddress() : "",
                    order.getDistrict() != null ? order.getDistrict() : "",
                    order.getTotalWeight(),
                    order.getItems() != null ? order.getItems().replace(",", ";") : "",
                    order.getIsManagementOfficeCollect() != null ? order.getIsManagementOfficeCollect() : false,
                    order.getPaymentLastFive() != null ? order.getPaymentLastFive() : "",
                    order.getStatus(),
                    order.getNotes() != null ? order.getNotes() : "",
                    order.getCreatedAt()
            ));
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "orders.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.toString().getBytes());
    }
}
