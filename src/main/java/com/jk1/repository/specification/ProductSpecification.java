package com.jk1.repository.specification;

import com.jk1.entity.Product;
import com.jk1.entity.enums.ProductStatus;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specifications for dynamic product filtering on the list page.
 *
 * <p><strong>Design notes:</strong></p>
 * <ul>
 *   <li>{@code query.distinct(true)} is applied to prevent duplicate rows
 *       caused by JOIN operations when multiple predicates are combined.</li>
 *   <li>Category and brand are fetched via LEFT JOIN (not FETCH) because
 *       Spring Data's {@code Page} support executes a separate COUNT query;
 *       mixing FETCH with count queries causes a
 *       {@code QueryException: query specified join fetching} error.
 *       The service layer's {@code @Transactional(readOnly=true)} keeps the
 *       session open so lazy access in the mapper still works correctly.</li>
 * </ul>
 */
public class ProductSpecification {

    private ProductSpecification() {
        // Utility class — no instantiation
    }

    /**
     * Builds a dynamic {@link Specification} for the product list/search page.
     *
     * <p>Only ACTIVE products are ever returned. All filter parameters are
     * optional; {@code null} values are safely ignored.</p>
     *
     * @param search      optional keyword (searched in name, description, category, brand)
     * @param categoryIds optional list of category IDs to include
     * @param brandIds    optional list of brand IDs to include
     * @param minPrice    optional minimum price (inclusive)
     * @param maxPrice    optional maximum price (inclusive)
     * @return a composed {@link Specification} ready for passing to
     *         {@code JpaSpecificationExecutor.findAll(spec, pageable)}
     */
    public static Specification<Product> searchAndFilter(
            String search,
            List<Long> categoryIds,
            List<Long> brandIds,
            Double minPrice,
            Double maxPrice) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Deduplicate rows that arise from implicit cross-joins
            if (query != null) {
                query.distinct(true);
            }

            // Explicit joins so predicates can reference association columns.
            // We use LEFT JOIN (not JOIN FETCH) to stay compatible with the
            // COUNT query that Spring Data executes for pagination.
            var categoryJoin = root.join("category", JoinType.LEFT);
            var brandJoin    = root.join("brand",    JoinType.LEFT);

            // ── Always: ACTIVE products only ────────────────────────────────
            predicates.add(cb.equal(root.get("productStatus"), ProductStatus.ACTIVE));

            // ── Keyword search ───────────────────────────────────────────────
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase().trim() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")),               pattern),
                        cb.like(cb.lower(root.get("description")),        pattern),
                        cb.like(cb.lower(categoryJoin.get("name")),       pattern),
                        cb.like(cb.lower(brandJoin.get("name")),          pattern)
                ));
            }

            // ── Category filter ──────────────────────────────────────────────
            if (categoryIds != null && !categoryIds.isEmpty()) {
                predicates.add(categoryJoin.get("id").in(categoryIds));
            }

            // ── Brand filter ─────────────────────────────────────────────────
            if (brandIds != null && !brandIds.isEmpty()) {
                predicates.add(brandJoin.get("id").in(brandIds));
            }

            // ── Price range ──────────────────────────────────────────────────
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("price"), BigDecimal.valueOf(minPrice)));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("price"), BigDecimal.valueOf(maxPrice)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
