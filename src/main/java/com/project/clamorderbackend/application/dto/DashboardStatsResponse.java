package com.project.clamorderbackend.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {

    private Long todayOrdersCount;
    private Integer totalWeight;
    private Long pendingOrdersCount;
}
