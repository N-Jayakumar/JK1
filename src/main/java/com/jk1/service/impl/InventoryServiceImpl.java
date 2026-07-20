package com.jk1.service.impl;

import com.jk1.entity.Inventory;
import com.jk1.repository.InventoryRepository;
import com.jk1.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        return inventoryRepository.findById(id);
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public Page<Inventory> findAll(Pageable pageable) {
        return inventoryRepository.findAll(pageable);
    }

    @Override
    public Page<Inventory> findAll(Specification<Inventory> spec, Pageable pageable) {
        return inventoryRepository.findAll(spec, pageable);
    }

    @Override
    public Inventory updateStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Inventory not found for product"));
        inventory.setQuantity(quantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory increaseStock(Long productId, Integer amount) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Inventory not found for product"));
        inventory.setQuantity(inventory.getQuantity() + amount);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory decreaseStock(Long productId, Integer amount) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Inventory not found for product"));
        if (inventory.getQuantity() < amount) {
            throw new RuntimeException("Insufficient stock");
        }
        inventory.setQuantity(inventory.getQuantity() - amount);
        return inventoryRepository.save(inventory);
    }

    @Override
    public List<Inventory> findLowStock(Integer threshold) {
        return inventoryRepository.findAll((root, query, cb) -> cb.lessThanOrEqualTo(root.get("quantity"), threshold));
    }

    @Override
    public List<Inventory> findOutOfStock() {
        return inventoryRepository.findAll((root, query, cb) -> cb.equal(root.get("quantity"), 0));
    }

    @Override
    public void deleteById(Long id) {
        inventoryRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return inventoryRepository.existsById(id);
    }
}
