package com.jk1.service;

import com.jk1.dto.response.ProductResponseDTO;
import com.jk1.entity.Product;
import com.jk1.mapper.ProductMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

/**
 * Service contract for Product catalog operations.
 *
 * All read operations that return entity collections should use
 * {@code @Transactional(readOnly = true)} in the implementation to
 * keep the Hibernate session open long enough for lazy association access.
 */
public interface ProductService {

    // ─── Write ──────────────────────────────────────────────────────────────
    Product save(Product product);
    void deleteById(Long id);

    // ─── Single record reads ─────────────────────────────────────────────────
    boolean existsById(Long id);
    Optional<Product> findById(Long id);
    Optional<Product> findBySlug(String slug);

    /**
     * Finds a product by slug with all lazy associations eagerly fetched
     * via JOIN FETCH. Use this in detail-page controllers.
     */
    Optional<Product> findBySlugWithDetails(String slug);

    /**
     * Finds a product by ID with all lazy associations eagerly fetched.
     */
    Optional<Product> findByIdWithDetails(Long id);

    // ─── Collection / Paged reads ────────────────────────────────────────────
    List<Product> findAll();
    Page<Product> findAll(Pageable pageable);
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    /**
     * Finds products by spec and maps them to DTOs inside a single
     * {@code @Transactional(readOnly=true)} boundary.
     *
     * <p>This is required when {@code spring.jpa.open-in-view=false}
     * because {@link Product} associations are lazy — mapping must happen
     * while the Hibernate session is still open.
     */
    Page<ProductResponseDTO> findAllAsDTO(Specification<Product> spec, Pageable pageable, ProductMapper mapper);

    /**
     * Active products belonging to a given category, identified by its slug.
     * Returns an empty page if the category slug does not exist.
     */
    Page<Product> findByCategorySlug(String slug, Pageable pageable);

    /**
     * Active products that are flagged as featured.
     */
    Page<Product> findFeaturedProducts(Pageable pageable);

    /**
     * Active products flagged as best-sellers.
     */
    Page<Product> findBestSellers(Pageable pageable);

    /**
     * Latest active products ordered by creation date descending.
     * Pass a {@link Pageable} with {@code Sort.by(Direction.DESC, "createdAt")}
     * if you want consistent ordering.
     */
    Page<Product> findLatestProducts(Pageable pageable);

    /**
     * Full-text search across name, description, category and brand name.
     * Only ACTIVE products are returned.
     */
    Page<Product> searchProducts(String query, Pageable pageable);

    /**
     * Lightweight autocomplete suggestions for the live-search dropdown.
     *
     * <p>Returns scalar projections (no full entity load) — immune to
     * {@link org.hibernate.LazyInitializationException}. Results are
     * capped at 8 and ordered category-name → product-name for consistency.</p>
     *
     * @param query trimmed, non-blank search keyword
     * @return list of at most 8 {@link com.jk1.dto.response.AutocompleteResponseDTO} items
     */
    List<com.jk1.dto.response.AutocompleteResponseDTO> findSuggestions(String query);


    /**
     * Active products in the same category as the given category ID,
     * excluding the specified product. Used for the "Related Products" rail.
     *
     * @param categoryId    category to search within
     * @param excludeId     product ID to exclude from results
     * @param limit         maximum number of products to return
     */
    List<Product> findRelatedProducts(Long categoryId, Long excludeId, int limit);
}
