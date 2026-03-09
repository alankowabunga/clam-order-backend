package com.project.clamorderbackend.application.dto;

import lombok.*;

/**
 * DTO for product response
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private String id;
    private String name;
    private Integer pricePerCatty;
    private String description;
    private Boolean isLimited;
    private Integer stockRemaining;
}
