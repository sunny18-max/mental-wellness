package com.saathvik.ecommerce.order.service;

import com.saathvik.ecommerce.order.client.InventoryClient;
import com.saathvik.ecommerce.order.dto.CreateOrderRequest;
import com.saathvik.ecommerce.order.dto.OrderItemRequest;
import com.saathvik.ecommerce.order.entity.Order;
import com.saathvik.ecommerce.order.entity.OrderItem;
import com.saathvik.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        List<OrderItem> items = new ArrayList<>();
        try {
            BigDecimal total = BigDecimal.ZERO;
            for (OrderItemRequest itemRequest : request.items()) {
                inventoryClient.reserve(itemRequest.productId(), itemRequest.quantity());
                total = total.add(itemRequest.unitPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
                items.add(OrderItem.builder()
                        .productId(itemRequest.productId())
                        .quantity(itemRequest.quantity())
                        .unitPrice(itemRequest.unitPrice())
                        .build());
            }

            Order order = Order.builder()
                    .customerId(request.customerId())
                    .totalAmount(total)
                    .status(Order.OrderStatus.CREATED)
                    .build();
            items.forEach(item -> {
                item.setOrder(order);
                order.getItems().add(item);
            });

            return orderRepository.save(order);
        } catch (RuntimeException ex) {
            items.forEach(item -> inventoryClient.release(item.getProductId(), item.getQuantity()));
            throw ex;
        }
    }

    public Order get(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));
    }

    public Order updateStatus(UUID id, Order.OrderStatus status) {
        Order order = get(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
