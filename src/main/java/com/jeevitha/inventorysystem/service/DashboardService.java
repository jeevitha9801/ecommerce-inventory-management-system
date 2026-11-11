package com.jeevitha.inventorysystem.service;

import com.jeevitha.inventorysystem.dto.DashboardStatsResponse;
import com.jeevitha.inventorysystem.entity.OrderStatus;
import com.jeevitha.inventorysystem.repository.OrderRepository;
import com.jeevitha.inventorysystem.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public DashboardStatsResponse getStats() {

        return DashboardStatsResponse.builder()
                .totalProducts(productRepository.count())
                .totalOrders(orderRepository.count())
                .availableStock(
                        productRepository.getTotalAvailableStock())
                .cancelledOrders(
                        orderRepository.countByStatus(
                                OrderStatus.CANCELLED))
                .build();
    }
}
