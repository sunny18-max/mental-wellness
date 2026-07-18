package com.saathvik.ecommerce.inventory.service;

import com.saathvik.ecommerce.inventory.entity.InventoryItem;
import com.saathvik.ecommerce.inventory.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;

    public InventoryItem getByProductId(UUID productId) {
        return inventoryItemRepository.findByProductId(productId)
                .orElseThrow(() -> new NoSuchElementException("No inventory record for product: " + productId));
    }

    public InventoryItem upsertStock(UUID productId, int quantity) {
        InventoryItem item = inventoryItemRepository.findByProductId(productId)
                .orElseGet(() -> InventoryItem.builder().productId(productId).quantityAvailable(0).build());
        item.setQuantityAvailable(item.getQuantityAvailable() + quantity);
        return inventoryItemRepository.save(item);
    }

    public synchronized InventoryItem reserve(UUID productId, int quantity) {
        InventoryItem item = getByProductId(productId);
        int free = item.getQuantityAvailable() - item.getQuantityReserved();
        if (free < quantity) {
            throw new IllegalStateException("Insufficient stock for product: " + productId);
        }
        item.setQuantityReserved(item.getQuantityReserved() + quantity);
        return inventoryItemRepository.save(item);
    }

    public InventoryItem release(UUID productId, int quantity) {
        InventoryItem item = getByProductId(productId);
        item.setQuantityReserved(Math.max(0, item.getQuantityReserved() - quantity));
        return inventoryItemRepository.save(item);
    }
}
