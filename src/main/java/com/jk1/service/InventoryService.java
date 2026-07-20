package com.jk1.service;

import com.jk1.entity.Inventory;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface InventoryService {
    Inventory save(Inventory inventory);
    Optional<Inventory> findById(Long id);
    List<Inventory> findAll();
    Page<Inventory> findAll(Pageable pageable);
    Page<Inventory> findAll(Specification<Inventory> spec, Pageable pageable);
    void deleteById(Long id);
    boolean existsById(Long id);
    
    Inventory updateStock(Long productId, Integer quantity);
    Inventory increaseStock(Long productId, Integer amount);
    Inventory decreaseStock(Long productId, Integer amount);
    List<Inventory> findLowStock(Integer threshold);
    List<Inventory> findOutOfStock();
}
