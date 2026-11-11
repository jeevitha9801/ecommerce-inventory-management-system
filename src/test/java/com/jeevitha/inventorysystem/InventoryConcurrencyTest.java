package com.jeevitha.inventorysystem;

import com.jeevitha.inventorysystem.dto.OrderRequest;
import com.jeevitha.inventorysystem.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class InventoryConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void testConcurrentOrders() throws Exception {

        ExecutorService executorService =
                Executors.newFixedThreadPool(2);

        Runnable task = () -> {

            try {

                OrderRequest request =
                        new OrderRequest();

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

        executorService.shutdown();

        executorService.awaitTermination(
                1,
                TimeUnit.MINUTES);
    }
}