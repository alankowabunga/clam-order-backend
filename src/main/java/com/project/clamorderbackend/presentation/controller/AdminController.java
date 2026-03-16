package com.project.clamorderbackend.presentation.controller;

import com.project.clamorderbackend.application.dto.*;
import com.project.clamorderbackend.application.usecase.*;
import com.project.clamorderbackend.domain.entity.Order;
import com.project.clamorderbackend.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderUseCase orderUseCase;
    private final OrderRepository orderRepository;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        
        long todayOrdersCount = orderRepository.countOrdersCreatedToday(startOfDay);
        long pendingOrdersCount = orderRepository.countByStatus(Order.OrderStatus.PENDING_PAYMENT);
        int totalWeight = orderRepository.sumTotalWeightToday(startOfDay);
        
        DashboardStatsResponse stats = DashboardStatsResponse.builder()
                .todayOrdersCount(todayOrdersCount)
                .totalWeight(totalWeight)
                .pendingOrdersCount(pendingOrdersCount)
                .build();
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/orders")
    public ResponseEntity<PagedResponse<OrderExportResponse>> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        int pageIndex = page - 1;
        PageRequest pageRequest = PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Order> orderPage;
        
        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            orderPage = orderRepository.findByCreatedAtBetween(start, end, pageRequest);
        } else {
            orderPage = orderRepository.findAllByOrderByCreatedAtDesc(pageRequest);
        }
        
        List<OrderExportResponse> content = orderPage.getContent().stream()
                .map(orderUseCase::toExportResponse)
                .toList();
        
        PagedResponse<OrderExportResponse> response = PagedResponse.<OrderExportResponse>builder()
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .currentPage(page)
                .pageSize(size)
                .content(content)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/export")
    public ResponseEntity<byte[]> exportOrders(
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Order> orders;
        
        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            orders = orderRepository.findByCreatedAtBetween(start, end);
        } else {
            orders = orderRepository.findAllOrderByCreatedAtDesc();
        }
        
        StringBuilder csv = new StringBuilder();
        csv.append("訂單編號,姓名,電話,配送方式,地址,地區,重量,項目,管理室代收,付款末五碼,狀態,狀態(中文),總金額,備註,建立時間\n");
        
        for (Order order : orders) {
            String itemsSummary = order.getItems().stream()
                    .map(item -> String.format("%s x%d", item.getProductName(), item.getQuantity()))
                    .collect(java.util.stream.Collectors.joining(";"));
            
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%d,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    order.getPublicId(),
                    order.getCustomerName(),
                    order.getPhone(),
                    order.getDeliveryMethod().name(),
                    order.getAddress() != null ? order.getAddress() : "",
                    order.getDistrict() != null ? order.getDistrict() : "",
                    order.getTotalWeight(),
                    itemsSummary,
                    order.getIsManagementOfficeCollect() != null ? order.getIsManagementOfficeCollect() : false,
                    order.getPaymentLastFive() != null ? order.getPaymentLastFive() : "",
                    order.getStatus().name(),
                    order.getStatusChinese(),
                    order.getTotalPay(),
                    order.getNotes() != null ? order.getNotes() : "",
                    order.getCreatedAt()
            ));
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv;charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "orders.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
