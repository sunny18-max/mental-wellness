package com.saathvik.ecommerce.payment.controller;

import com.saathvik.ecommerce.payment.dto.ChargeRequest;
import com.saathvik.ecommerce.payment.entity.Payment;
import com.saathvik.ecommerce.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/charge")
    public ResponseEntity<Payment> charge(@Valid @RequestBody ChargeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.charge(request));
    }

    @GetMapping("/{id}")
    public Payment get(@PathVariable UUID id) {
        return paymentService.get(id);
    }

    @GetMapping("/order/{orderId}")
    public Payment getByOrder(@PathVariable UUID orderId) {
        return paymentService.getByOrderId(orderId);
    }
}
