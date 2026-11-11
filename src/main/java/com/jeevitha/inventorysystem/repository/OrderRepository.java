package com.jeevitha.inventorysystem.repository;

import com.jeevitha.inventorysystem.entity.Order;
import com.jeevitha.inventorysystem.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    long countByStatus(OrderStatus status);
}
