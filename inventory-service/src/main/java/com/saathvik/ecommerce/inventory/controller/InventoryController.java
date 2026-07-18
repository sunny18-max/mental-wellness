package com.saathvik.ecommerce.inventory.controller;

import com.saathvik.ecommerce.inventory.dto.ReserveRequest;
import com.saathvik.ecommerce.inventory.entity.InventoryItem;
import com.saathvik.ecommerce.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public InventoryItem get(@PathVariable UUID productId) {
        return inventoryService.getByProductId(productId);
    }

    @PostMapping("/{productId}/stock")
    public InventoryItem addStock(@PathVariable UUID productId, @RequestParam int quantity) {
        return inventoryService.upsertStock(productId, quantity);
    }

    @PostMapping("/reserve")
    public InventoryItem reserve(@Valid @RequestBody ReserveRequest request) {
        return inventoryService.reserve(request.productId(), request.quantity());
    }

    @PostMapping("/release")
    public InventoryItem release(@Valid @RequestBody ReserveRequest request) {
        return inventoryService.release(request.productId(), request.quantity());
    }
}
