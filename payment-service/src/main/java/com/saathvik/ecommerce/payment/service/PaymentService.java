package com.saathvik.ecommerce.payment.service;

import com.saathvik.ecommerce.payment.dto.ChargeRequest;
import com.saathvik.ecommerce.payment.entity.Payment;
import com.saathvik.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment charge(ChargeRequest request) {
        Payment payment = Payment.builder()
                .orderId(request.orderId())
                .amount(request.amount())
                .method(request.method())
                .status(simulateGateway())
                .build();
        return paymentRepository.save(payment);
    }

    public Payment get(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Payment not found: " + id));
    }

    public Payment getByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoSuchElementException("No payment for order: " + orderId));
    }

    private Payment.PaymentStatus simulateGateway() {
        return ThreadLocalRandom.current().nextInt(100) < 90
                ? Payment.PaymentStatus.SUCCESS
                : Payment.PaymentStatus.FAILED;
    }
}
