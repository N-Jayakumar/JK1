package com.jk1.repository;

import com.jk1.entity.Product;
import com.jk1.entity.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // ─── Single record lookups (with JOIN FETCH to prevent LazyInitializationException) ───

    /**
     * Find a product by slug with all lazy associations eagerly loaded.
     * Prevents LazyInitializationException when the session closes after the method returns.
     */
    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.brand " +
           "LEFT JOIN FETCH p.inventory " +
           "LEFT JOIN FETCH p.images " +
           "WHERE p.slug = :slug")
    Optional<Product> findBySlugWithDetails(@Param("slug") String slug);

    /**
     * Find a product by ID with all lazy associations eagerly loaded.
     */
    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.brand " +
           "LEFT JOIN FETCH p.inventory " +
           "LEFT JOIN FETCH p.images " +
           "WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Long id);

    // ─── Simple derived finders ───

    Optional<Product> findBySlug(String slug);
    Optional<Product> findBySku(String sku);
    List<Product> findByProductStatus(ProductStatus status);

    // ─── Category / Brand scoped finders ───

    /**
     * Active products by category internal ID — used for related products.
     * Uses JOIN FETCH to load associations safely.
     */
    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category c " +
           "LEFT JOIN FETCH p.brand " +
           "LEFT JOIN FETCH p.inventory " +
           "LEFT JOIN FETCH p.images " +
           "WHERE c.id = :categoryId AND p.productStatus = 'ACTIVE'")
    List<Product> findActiveByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Paginated active products filtered by category slug.
     * Used by GET /products/category/{slug}.
     *
     * NOTE: Spring Data JPA does not support JOIN FETCH with pagination (causes
     * HHH90003004 warning + incorrect counts). We use a two-query approach:
     * the count query is separated so Hibernate can handle it correctly.
     */
    @Query(value = "SELECT DISTINCT p FROM Product p " +
                   "JOIN p.category c " +
                   "LEFT JOIN FETCH p.brand " +
                   "LEFT JOIN FETCH p.inventory " +
                   "LEFT JOIN FETCH p.images " +
                   "WHERE c.slug = :slug AND p.productStatus = 'ACTIVE'",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Product p " +
                        "JOIN p.category c " +
                        "WHERE c.slug = :slug AND p.productStatus = 'ACTIVE'")
    Page<Product> findByCategorySlug(@Param("slug") String slug, Pageable pageable);

    /**
     * Paginated active products filtered by brand ID.
     */
    @Query(value = "SELECT DISTINCT p FROM Product p " +
                   "JOIN p.brand b " +
                   "LEFT JOIN FETCH p.category " +
                   "LEFT JOIN FETCH p.inventory " +
                   "LEFT JOIN FETCH p.images " +
                   "WHERE b.id = :brandId AND p.productStatus = 'ACTIVE'",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Product p " +
                        "JOIN p.brand b " +
                        "WHERE b.id = :brandId AND p.productStatus = 'ACTIVE'")
    Page<Product> findByBrandId(@Param("brandId") Long brandId, Pageable pageable);

    // ─── Status-scoped paged finders ───

    Page<Product> findByProductStatus(ProductStatus status, Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Product p " +
                   "LEFT JOIN FETCH p.category " +
                   "LEFT JOIN FETCH p.brand " +
                   "LEFT JOIN FETCH p.inventory " +
                   "LEFT JOIN FETCH p.images " +
                   "WHERE p.featured = true AND p.productStatus = :status",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Product p " +
                        "WHERE p.featured = true AND p.productStatus = :status")
    Page<Product> findByFeaturedTrueAndProductStatus(@Param("status") ProductStatus status, Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Product p " +
                   "LEFT JOIN FETCH p.category " +
                   "LEFT JOIN FETCH p.brand " +
                   "LEFT JOIN FETCH p.inventory " +
                   "LEFT JOIN FETCH p.images " +
                   "WHERE p.bestSeller = true AND p.productStatus = :status",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Product p " +
                        "WHERE p.bestSeller = true AND p.productStatus = :status")
    Page<Product> findByBestSellerTrueAndProductStatus(@Param("status") ProductStatus status, Pageable pageable);

    // ─── Autocomplete / Suggestions ───

    /**
     * Lightweight suggestions query for the live-search autocomplete dropdown.
     *
     * <p><strong>Design decisions:</strong></p>
     * <ul>
     *   <li>Returns {@code Object[]} projections (scalar columns) instead of full
     *       entities. This avoids any lazy-loading risk — category, brand, and the
     *       primary image URL are read as plain SQL values in a single JOIN query.</li>
     *   <li>Uses {@code INNER JOIN} for category and brand (both are non-nullable
     *       in the schema) and {@code LEFT JOIN} for images (a product may have none).</li>
     *   <li>The {@code CASE} expression picks the primary image first; if none is
     *       flagged as primary, the first available image is used via ordering.</li>
     *   <li>Sorted by {@code category.name ASC, p.name ASC} for a consistent,
     *       predictable dropdown order.</li>
     *   <li>Caller passes a {@link Pageable} with {@code size = 8} to cap results.</li>
     * </ul>
     *
     * <p>Column order in the returned {@code Object[]}:</p>
     * <ol start="0">
     *   <li>p.id        (Long)</li>
     *   <li>p.name      (String)</li>
     *   <li>p.slug      (String)</li>
     *   <li>c.name      (String) — category name</li>
     *   <li>c.slug      (String) — category slug</li>
     *   <li>b.name      (String) — brand name</li>
     *   <li>pi.imageUrl (String) — first image URL, may be null</li>
     *   <li>p.price     (BigDecimal)</li>
     *   <li>p.discountPrice (BigDecimal) — may be null</li>
     * </ol>
     *
     * @param query keyword to search (already trimmed, caller ensures non-blank)
     * @param pageable must have size = 8, no sort (query defines ORDER BY)
     * @return list of scalar projections, at most {@code pageable.getPageSize()} rows
     */
    @Query("SELECT p.id, p.name, p.slug, " +
           "       c.name, c.slug, " +
           "       b.name, " +
           "       (SELECT pi.imageUrl FROM ProductImage pi WHERE pi.product = p AND pi.isPrimary = true), " +
           "       p.price, p.discountPrice " +
           "FROM Product p " +
           "JOIN p.category c " +
           "JOIN p.brand b " +
           "WHERE p.productStatus = 'ACTIVE' " +
           "  AND (LOWER(p.name)  LIKE LOWER(CONCAT('%', :query, '%')) " +
           "   OR  LOWER(c.name)  LIKE LOWER(CONCAT('%', :query, '%')) " +
           "   OR  LOWER(b.name)  LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY c.name ASC, p.name ASC")
    List<Object[]> findSuggestions(@Param("query") String query, Pageable pageable);

    // ─── Search ───

    /**
     * Full-text search across name, description, category name, and brand name.
     * Only ACTIVE products are returned.
     */
    @Query(value = "SELECT DISTINCT p FROM Product p " +
                   "LEFT JOIN FETCH p.category c " +
                   "LEFT JOIN FETCH p.brand b " +
                   "LEFT JOIN FETCH p.inventory " +
                   "LEFT JOIN FETCH p.images " +
                   "WHERE p.productStatus = :status " +
                   "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
                   "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
                   "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
                   "OR LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')))",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Product p " +
                        "LEFT JOIN p.category c " +
                        "LEFT JOIN p.brand b " +
                        "WHERE p.productStatus = :status " +
                        "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "OR LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> searchProducts(@Param("query") String query,
                                 @Param("status") ProductStatus status,
                                 Pageable pageable);
}
