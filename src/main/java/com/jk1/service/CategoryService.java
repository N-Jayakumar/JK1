package com.jk1.service;

import com.jk1.entity.Category;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CategoryService {
    Category save(Category category);
    Optional<Category> findById(Long id);
    List<Category> findAll();
    Page<Category> findAll(Pageable pageable);
    Page<Category> findAll(Specification<Category> spec, Pageable pageable);
    void deleteById(Long id);
    boolean existsById(Long id);
}
