package com.jk1.service;

import com.jk1.entity.Coupon;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CouponService {
    Coupon save(Coupon coupon);
    Optional<Coupon> findById(Long id);
    List<Coupon> findAll();
    Page<Coupon> findAll(Pageable pageable);
    Page<Coupon> findAll(Specification<Coupon> spec, Pageable pageable);
    void deleteById(Long id);
    boolean existsById(Long id);
}
