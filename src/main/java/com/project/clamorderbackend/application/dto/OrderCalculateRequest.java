package com.project.clamorderbackend.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * DTO for order calculation request
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCalculateRequest {

    @NotNull(message = "商品列表不能為空")
    @Size(min = 1, message = "請至少選擇一項商品")
    private List<OrderItemDto> items;

    @NotBlank(message = "配送方式不能為空")
    private String deliveryMethod;

    private String district;

    private String address;

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
