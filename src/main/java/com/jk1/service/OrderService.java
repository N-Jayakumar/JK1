package com.jk1.service;

import com.jk1.entity.Order;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface OrderService {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
    Page<Order> findAll(Pageable pageable);
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);
    void deleteById(Long id);
    boolean existsById(Long id);

    Order placeOrder(String email, com.jk1.dto.request.OrderRequestDTO dto);
    List<Order> getUserOrders(String email);
    Order getOrderDetails(String email, String orderNumber);
}
