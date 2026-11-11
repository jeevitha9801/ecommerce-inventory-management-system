package com.jeevitha.inventorysystem.controller;

import com.jeevitha.inventorysystem.dto.OrderRequest;
import com.jeevitha.inventorysystem.dto.OrderResponse;
import com.jeevitha.inventorysystem.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(
            @Valid @RequestBody OrderRequest request) {

        return orderService.createOrder(request);
    }
    @GetMapping
    public List<OrderResponse> getAllOrders() {

        return orderService.getAllOrders();
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancelOrder(
            @PathVariable Long id) {

        return orderService.cancelOrder(id);
    }

    @PostMapping("/concurrency-test")
    public String concurrencyTest() {

        ExecutorService executorService =
                Executors.newFixedThreadPool(2);

        Runnable task = () -> {

            try {

                OrderRequest request = new OrderRequest();
                request.setProductId(1L);
                request.setQuantity(1);

                orderService.createOrder(request);

                System.out.println(
                        Thread.currentThread().getName()
                                + " SUCCESS");

            } catch (Exception e) {

                System.out.println(
                        Thread.currentThread().getName()
                                + " FAILED : "
                                + e.getMessage());
            }
        };

        executorService.submit(task);
        executorService.submit(task);

        return "Concurrency Test Triggered";
    }
    @PostMapping("/pessimistic")
    public OrderResponse createOrderPessimistic(
            @RequestBody OrderRequest request) {

        return orderService
                .createOrderWithPessimisticLock(request);
    }

    @PostMapping("/pessimistic-test")
    public String pessimisticTest() {

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        Runnable task = () -> {

            try {

                OrderRequest request =
                        new OrderRequest();

                request.setProductId(1L);
                request.setQuantity(1);

                orderService
                        .createOrderWithPessimisticLock(
                                request);

                System.out.println(
                        Thread.currentThread().getName()
                                + " SUCCESS");

            } catch (Exception e) {

                System.out.println(
                        Thread.currentThread().getName()
                                + " FAILED : "
                                + e.getMessage());
            }
        };

        executor.submit(task);
        executor.submit(task);

        return "Pessimistic test triggered";
    }

}
