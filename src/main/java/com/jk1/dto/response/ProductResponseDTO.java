package com.jk1.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * Flat DTO projected from the Product entity for use in Thymeleaf templates
 * and JSON search API responses.
 *
 * All association data (category, brand, inventory, images) is denormalized
 * here so templates never touch JPA proxies directly, eliminating any risk
 * of LazyInitializationException.
 */
@Data
public class ProductResponseDTO {

    // ─── Identity ───────────────────────────────────────────────────────────
    private Long   id;
    private String name;
    private String slug;
    private String sku;
    private String description;

    // ─── Pricing ────────────────────────────────────────────────────────────
    private BigDecimal price;
    private BigDecimal discountPrice;
    /** Computed field: percentage off if discountPrice is set, otherwise 0.0 */
    private double discountPercentage;

    // ─── Category ───────────────────────────────────────────────────────────
    private Long   categoryId;
    private String categoryName;
    private String categorySlug;

    // ─── Brand ──────────────────────────────────────────────────────────────
    private Long   brandId;
    private String brandName;
    private String brandLogoUrl;

    // ─── Media ──────────────────────────────────────────────────────────────
    /** Primary image URL — safe fallback to placeholder if no images exist. */
    private String       imageUrl;
    private List<String> allImageUrls;

    // ─── Ratings / Reviews ──────────────────────────────────────────────────
    /** Stored rating on the entity (BigDecimal → double for template math). */
    private double  averageRating;
    private int     reviewCount;

    // ─── Stock ──────────────────────────────────────────────────────────────
    private int stockQuantity;

    // ─── Flags ──────────────────────────────────────────────────────────────
    private boolean featured;
    private boolean bestSeller;

    // ─── Status ─────────────────────────────────────────────────────────────
    /** String representation of ProductStatus enum (e.g., "ACTIVE"). */
    private String productStatus;
}
