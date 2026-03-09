package com.project.clamorderbackend.application.dto;

import lombok.*;

/**
 * DTO for order export response
 */
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
    private String notes;
    private String createdAt;
}
