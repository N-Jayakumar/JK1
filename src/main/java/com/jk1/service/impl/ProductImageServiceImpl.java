package com.jk1.service.impl;

import com.jk1.entity.ProductImage;
import com.jk1.repository.ProductImageRepository;
import com.jk1.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;

    @Override
    public ProductImage save(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }

    @Override
    public Optional<ProductImage> findById(Long id) {
        return productImageRepository.findById(id);
    }

    @Override
    public List<ProductImage> findAll() {
        return productImageRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        productImageRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return productImageRepository.existsById(id);
    }
}
