package com.jk1.service;

import com.jk1.entity.Review;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ReviewService {
    Review save(Review review);
    Optional<Review> findById(Long id);
    List<Review> findAll();
    Page<Review> findAll(Pageable pageable);
    Page<Review> findAll(Specification<Review> spec, Pageable pageable);
    void deleteById(Long id);
    boolean existsById(Long id);
}
