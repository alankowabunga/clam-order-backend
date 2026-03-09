package com.project.clamorderbackend.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * DTO for order submission request
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSubmitRequest {

    @NotBlank(message = "姓名不能為空")
    private String customerName;

    @NotBlank(message = "電話不能為空")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "電話格式不正確")
    private String phone;

    @NotBlank(message = "配送方式不能為空")
    private String deliveryMethod;

    private String address;

    private String district;

    @NotNull(message = "商品列表不能為空")
    @Size(min = 1, message = "請至少選擇一項商品")
    @Valid
    private List<OrderItemDto> items;

    private Boolean isManagementOfficeCollect;

    @Pattern(regexp = "^[0-9]{5}$", message = "帳號後五碼必須是5位數字")
    private String paymentLastFive;

    private String notes;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        
        @NotBlank(message = "商品ID不能為空")
        private String productId;

        @NotNull(message = "數量不能為空")
        @Min(value = 1, message = "數量至少為1")
        private Integer qty;
    }
}
