package com.jeevitha.inventorysystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DashboardStatsResponse {

    private long totalProducts;

    private long totalOrders;

    private int availableStock;

    private long cancelledOrders;
}