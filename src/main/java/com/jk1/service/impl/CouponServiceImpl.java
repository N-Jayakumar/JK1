package com.jk1.service.impl;

import com.jk1.entity.Coupon;
import com.jk1.repository.CouponRepository;
import com.jk1.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public Coupon save(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    public Optional<Coupon> findById(Long id) {
        return couponRepository.findById(id);
    }

    @Override
    public List<Coupon> findAll() {
        return couponRepository.findAll();
    }

    @Override
    public Page<Coupon> findAll(Pageable pageable) {
        return couponRepository.findAll(pageable);
    }

    @Override
    public Page<Coupon> findAll(Specification<Coupon> spec, Pageable pageable) {
        return couponRepository.findAll(spec, pageable);
    }

    @Override
    public void deleteById(Long id) {
        couponRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return couponRepository.existsById(id);
    }
}
