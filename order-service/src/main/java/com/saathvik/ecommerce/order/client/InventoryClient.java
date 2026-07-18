package com.saathvik.ecommerce.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(@Value("${services.inventory-url}") String inventoryUrl) {
        this.restClient = RestClient.create(inventoryUrl);
    }

    public void reserve(UUID productId, int quantity) {
        restClient.post()
                .uri("/api/v1/inventory/reserve")
                .body(Map.of("productId", productId, "quantity", quantity))
                .retrieve()
                .toBodilessEntity();
    }

    public void release(UUID productId, int quantity) {
        restClient.post()
                .uri("/api/v1/inventory/release")
                .body(Map.of("productId", productId, "quantity", quantity))
                .retrieve()
                .toBodilessEntity();
    }
}
