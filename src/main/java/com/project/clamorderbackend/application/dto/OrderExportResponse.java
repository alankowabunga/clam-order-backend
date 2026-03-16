package com.project.clamorderbackend.application.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderExportResponse {

    private String orderId;
    private String customerName;
    private String phone;
    private String deliveryMethod;
    private String address;
    private String district;
    private Integer totalWeight;
    private String items;
    private Boolean isManagementOfficeCollect;
    private String paymentLastFive;
    private String status;
    private String statusChinese;
    private BigDecimal finalAmount;
    private String notes;
    private String createdAt;
}
