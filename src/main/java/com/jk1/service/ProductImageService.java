package com.jk1.service;

import com.jk1.entity.ProductImage;
import java.util.List;
import java.util.Optional;

public interface ProductImageService {
    ProductImage save(ProductImage productImage);
    Optional<ProductImage> findById(Long id);
    List<ProductImage> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}
