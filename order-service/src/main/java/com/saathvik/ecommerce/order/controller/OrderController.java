package com.saathvik.ecommerce.order.controller;

import com.saathvik.ecommerce.order.dto.CreateOrderRequest;
import com.saathvik.ecommerce.order.entity.Order;
import com.saathvik.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable UUID id) {
        return orderService.get(id);
    }

    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable UUID id, @RequestParam Order.OrderStatus status) {
        return orderService.updateStatus(id, status);
    }
}
