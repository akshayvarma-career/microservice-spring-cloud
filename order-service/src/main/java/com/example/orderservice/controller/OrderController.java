package com.example.orderservice.controller;

import com.example.orderservice.client.ProductClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final ProductClient productClient;

    public OrderController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @GetMapping
    @Retry(name = "productService", fallbackMethod = "retryFallback")
    @CircuitBreaker(name = "productService", fallbackMethod = "productFallback")
    public String placeOrder() {
        List<String> products = productClient.getProducts();
        return "Order placed for: " + products.get(0);
    }

    public String productFallback(Throwable t) {
        return "Order placed for: default-product (product-service unavailable)";
    }

    public String retryFallback(Throwable t) {
        return "Order placed with degraded behavior (retry failed)";
    }
}
