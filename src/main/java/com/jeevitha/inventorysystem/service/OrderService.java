package com.jeevitha.inventorysystem.service;

import com.jeevitha.inventorysystem.dto.OrderRequest;
import com.jeevitha.inventorysystem.dto.OrderResponse;
import com.jeevitha.inventorysystem.entity.Order;
import com.jeevitha.inventorysystem.entity.OrderStatus;
import com.jeevitha.inventorysystem.entity.Product;
import com.jeevitha.inventorysystem.exception.ConcurrentUpdateException;
import com.jeevitha.inventorysystem.exception.InsufficientStockException;
import com.jeevitha.inventorysystem.exception.OrderAlreadyCancelledException;
import com.jeevitha.inventorysystem.exception.ProductNotFoundException;
import com.jeevitha.inventorysystem.repository.OrderRepository;
import com.jeevitha.inventorysystem.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        try {

            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() ->
                            new ProductNotFoundException("Product not found with id: "
                                    + request.getProductId()));

            if (product.getStock() < request.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock available");
            }

            System.out.println(
                    Thread.currentThread().getName()
                            + " Read Stock = "
                            + product.getStock());

            product.setStock(
                    product.getStock() - request.getQuantity());

            productRepository.save(product);

            Order order = Order.builder()
                    .product(product)
                    .quantity(request.getQuantity())
                    .status(OrderStatus.PLACED)
                    .createdAt(LocalDateTime.now())
                    .build();

            Order savedOrder = orderRepository.save(order);

            return mapToResponse(savedOrder);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConcurrentUpdateException(
                    "Product was modified by another user.");
        }
    }

    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order is not found with Id: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {

            throw new OrderAlreadyCancelledException(
                    "Order already cancelled");
        }

        Product product = order.getProduct();

        product.setStock(
                product.getStock() + order.getQuantity());

        productRepository.save(product);

        order.setStatus(OrderStatus.CANCELLED);

        Order cancelledOrder = orderRepository.save(order);

        return mapToResponse(cancelledOrder);

    }

    private OrderResponse mapToResponse(Order order) {

        return OrderResponse.builder()
                .id(order.getId())
                .productId(order.getProduct().getId())
                .productName(order.getProduct().getName())
                .quantity(order.getQuantity())
                .status(order.getStatus().name())
                .build();
    }

    @Transactional
    public OrderResponse createOrderWithPessimisticLock(
            OrderRequest request) {

        System.out.println(
                Thread.currentThread().getName()
                        + " trying to acquire lock");

        Product product = productRepository
                .findByIdForUpdate(request.getProductId())
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found"));

        System.out.println(
                Thread.currentThread().getName()
                        + " acquired lock");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(
                Thread.currentThread().getName()
                        + " waking up and processing order");

        if (product.getStock() < request.getQuantity()) {

            throw new InsufficientStockException(
                    "Insufficient stock available");
        }

        product.setStock(
                product.getStock() - request.getQuantity());

        productRepository.save(product);

        Order order = Order.builder()
                .product(product)
                .quantity(request.getQuantity())
                .status(OrderStatus.PLACED)
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }
}
