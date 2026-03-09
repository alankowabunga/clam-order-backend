package com.project.clamorderbackend.application.dto;

import lombok.*;

/**
 * DTO for order submission response
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSubmitResponse {

    private String orderId;
    private String status;
    private String totalPay;
    private String message;
}
