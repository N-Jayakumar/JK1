package com.jk1.service;

import com.jk1.entity.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    List<Payment> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}
