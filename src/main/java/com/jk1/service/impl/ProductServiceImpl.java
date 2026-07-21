package com.jk1.service.impl;

import com.jk1.dto.response.AutocompleteResponseDTO;
import com.jk1.dto.response.ProductResponseDTO;
import com.jk1.entity.Product;
import com.jk1.entity.enums.ProductStatus;
import com.jk1.mapper.ProductMapper;
import com.jk1.repository.ProductRepository;
import com.jk1.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ProductService}.
 *
 * <p>All read methods are annotated {@code @Transactional(readOnly = true)} to
 * keep the Hibernate session open while the service method executes, preventing
 * {@link org.hibernate.LazyInitializationException} when lazy associations are
 * touched during mapping inside the same call.</p>
 *
 * <p>Write methods use {@code @Transactional} (read-write) to ensure
 * rollback on failure.</p>
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    // ─── Write ──────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    // ─── Single record reads ─────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findBySlug(String slug) {
        return productRepository.findBySlug(slug);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findBySlugWithDetails(String slug) {
        return productRepository.findBySlugWithDetails(slug);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findByIdWithDetails(Long id) {
        return productRepository.findByIdWithDetails(id);
    }

    // ─── Collection / Paged reads ────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAll(Specification<Product> spec, Pageable pageable) {
        return productRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findAllAsDTO(Specification<Product> spec, Pageable pageable, ProductMapper mapper) {
        // Mapping happens INSIDE this @Transactional boundary so that lazy
        // associations (category, brand, inventory, images) are accessible
        // even when spring.jpa.open-in-view=false.
        return productRepository.findAll(spec, pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findByCategorySlug(String slug, Pageable pageable) {
        if (slug == null || slug.isBlank()) {
            return Page.empty(pageable);
        }
        return productRepository.findByCategorySlug(slug, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findFeaturedProducts(Pageable pageable) {
        return productRepository.findByFeaturedTrueAndProductStatus(ProductStatus.ACTIVE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findBestSellers(Pageable pageable) {
        return productRepository.findByBestSellerTrueAndProductStatus(ProductStatus.ACTIVE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findLatestProducts(Pageable pageable) {
        // Enforce createdAt DESC ordering regardless of what the caller passes
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by("id"))
        );
        return productRepository.findByProductStatus(ProductStatus.ACTIVE, sortedPageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return Page.empty(pageable);
        }
        return productRepository.searchProducts(query.trim(), ProductStatus.ACTIVE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findRelatedProducts(Long categoryId, Long excludeId, int limit) {
        if (categoryId == null) {
            return List.of();
        }
        return productRepository.findActiveByCategoryId(categoryId)
                .stream()
                .filter(p -> !p.getId().equals(excludeId))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Maps raw {@code Object[]} projections from {@link ProductRepository#findSuggestions}
     * to {@link AutocompleteResponseDTO} instances.
     *
     * <p>Deduplication: a {@link LinkedHashSet} of product names (case-insensitive) is used
     * to guarantee no duplicate suggestions reach the frontend — even if the JPQL query
     * returns the same product multiple times due to a join fan-out.</p>
     *
     * <p>Column mapping (matches the SELECT order in the repository query):</p>
     * <pre>
     *   [0] p.id           Long
     *   [1] p.name         String
     *   [2] p.slug         String
     *   [3] c.name         String  (category)
     *   [4] c.slug         String  (category)
     *   [5] b.name         String  (brand)
     *   [6] pi.imageUrl    String  (may be null)
     *   [7] p.price        BigDecimal
     *   [8] p.discountPrice BigDecimal (may be null)
     * </pre>
     */
    @Override
    @Transactional(readOnly = true)
    public List<AutocompleteResponseDTO> findSuggestions(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String trimmed = query.trim();
        // Pageable: size=8, unsorted — ORDER BY is defined inside the JPQL query
        Pageable pageable = PageRequest.of(0, 8, Sort.unsorted());
        List<Object[]> rows = productRepository.findSuggestions(trimmed, pageable);

        // Deduplicate by product name (case-insensitive) while preserving order
        Set<String> seenNames = new LinkedHashSet<>();
        List<AutocompleteResponseDTO> suggestions = new ArrayList<>(rows.size());

        for (Object[] row : rows) {
            String name = safeString(row, 1);
            if (name.isBlank() || !seenNames.add(name.toLowerCase())) {
                continue; // skip blank or duplicate
            }

            AutocompleteResponseDTO dto = new AutocompleteResponseDTO();
            dto.setId(row[0] instanceof Long l ? l : ((Number) row[0]).longValue());
            dto.setName(name);
            dto.setSlug(safeString(row, 2));
            dto.setCategoryName(safeString(row, 3));
            dto.setCategorySlug(safeString(row, 4));
            dto.setBrandName(safeString(row, 5));

            // Image: use stored image, or the global fallback
            String imgUrl = safeString(row, 6);
            dto.setImageUrl(imgUrl.isBlank()
                    ? "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?q=80&w=100&auto=format&fit=crop"
                    : imgUrl);

            BigDecimal price        = row[7] instanceof BigDecimal bd ? bd : null;
            BigDecimal discountPrice = row[8] instanceof BigDecimal bd ? bd : null;

            dto.setPrice(price);
            dto.setDiscountPrice(discountPrice);

            suggestions.add(dto);
        }

        return suggestions;
    }

    /** Safe null-to-empty-string accessor for Object[] projection columns. */
    private static String safeString(Object[] row, int index) {
        if (index >= row.length || row[index] == null) return "";
        return row[index].toString();
    }
}
