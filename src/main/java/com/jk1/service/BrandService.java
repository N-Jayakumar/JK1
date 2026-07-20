package com.jk1.service;

import com.jk1.entity.Brand;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface BrandService {
    Brand save(Brand brand);
    Optional<Brand> findById(Long id);
    List<Brand> findAll();
    Page<Brand> findAll(Pageable pageable);
    Page<Brand> findAll(Specification<Brand> spec, Pageable pageable);
    void deleteById(Long id);
    boolean existsById(Long id);
}
