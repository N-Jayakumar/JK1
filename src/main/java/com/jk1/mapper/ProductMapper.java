package com.jk1.mapper;

import com.jk1.dto.request.ProductRequestDTO;
import com.jk1.dto.response.ProductResponseDTO;
import com.jk1.entity.Product;
import com.jk1.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps between {@link Product} JPA entity and its DTO representations.
 *
 * <p><strong>Safety contract:</strong> All association access (category, brand,
 * inventory, images) is null-checked. This mapper is safe to call after session
 * closure <em>only if</em> the calling repository method used JOIN FETCH to load
 * the associations eagerly. The detail controller ({@code /products/{slug}}) uses
 * {@code findBySlugWithDetails} which does exactly this.</p>
 */
@Component
public class ProductMapper {

    private static final String FALLBACK_IMAGE =
            "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?q=80&w=800&auto=format&fit=crop";

    // ─── Entity → Request DTO ────────────────────────────────────────────────

    public Product toEntity(ProductRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Product entity = new Product();
        // Additional field mapping done by the admin service layer
        return entity;
    }

    // ─── Entity → Response DTO ──────────────────────────────────────────────

    /**
     * Converts a {@link Product} entity to a {@link ProductResponseDTO}.
     *
     * All association accesses are guarded; a null association yields a safe
     * default (empty string, 0, false, fallback image) rather than an NPE.
     *
     * @param entity the product entity (must not be null)
     * @return populated response DTO
     */
    public ProductResponseDTO toResponseDTO(Product entity) {
        if (entity == null) {
            return null;
        }

        ProductResponseDTO dto = new ProductResponseDTO();

        // ── Identity ──────────────────────────────────────────────────────
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setSku(entity.getSku());
        dto.setDescription(entity.getDescription());

        // ── Pricing ───────────────────────────────────────────────────────
        dto.setPrice(entity.getPrice());
        dto.setDiscountPrice(entity.getDiscountPrice());
        dto.setDiscountPercentage(computeDiscountPercentage(entity.getPrice(), entity.getDiscountPrice()));

        // ── Category ──────────────────────────────────────────────────────
        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
            dto.setCategoryName(entity.getCategory().getName());
            dto.setCategorySlug(entity.getCategory().getSlug());
        } else {
            dto.setCategoryId(null);
            dto.setCategoryName("");
            dto.setCategorySlug("");
        }

        // ── Brand ─────────────────────────────────────────────────────────
        if (entity.getBrand() != null) {
            dto.setBrandId(entity.getBrand().getId());
            dto.setBrandName(entity.getBrand().getName());
            dto.setBrandLogoUrl(entity.getBrand().getLogoUrl());
        } else {
            dto.setBrandId(null);
            dto.setBrandName("");
            dto.setBrandLogoUrl("");
        }

        // ── Media ─────────────────────────────────────────────────────────
        if (entity.getImages() != null && !entity.getImages().isEmpty()) {
            // Primary image first
            String primary = entity.getImages().stream()
                    .filter(ProductImage::isPrimary)
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(entity.getImages().get(0).getImageUrl());
            dto.setImageUrl(primary);

            List<String> all = entity.getImages().stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());
            dto.setAllImageUrls(all);
        } else {
            dto.setImageUrl(FALLBACK_IMAGE);
            dto.setAllImageUrls(List.of(FALLBACK_IMAGE));
        }

        // ── Ratings ───────────────────────────────────────────────────────
        dto.setAverageRating(
                entity.getRating() != null
                        ? entity.getRating().doubleValue()
                        : 0.0
        );
        dto.setReviewCount(
                entity.getReviewCount() != null
                        ? entity.getReviewCount()
                        : 0
        );

        // ── Stock ─────────────────────────────────────────────────────────
        dto.setStockQuantity(entity.getQuantity() != null ? entity.getQuantity() : 0);

        // ── Flags ─────────────────────────────────────────────────────────
        dto.setFeatured(entity.isFeatured());
        dto.setBestSeller(entity.isBestSeller());

        // ── Status ────────────────────────────────────────────────────────
        dto.setProductStatus(
                entity.getProductStatus() != null
                        ? entity.getProductStatus().name()
                        : "DRAFT"
        );

        return dto;
    }

    // ─── Helpers ────────────────────────────────────────────────────────────

    /**
     * Computes the discount percentage given original price and discount price.
     *
     * @param price         original price (must not be null)
     * @param discountPrice optional discount price
     * @return percentage as a double (e.g., 15.0 for 15%), or 0.0 if no discount
     */
    private double computeDiscountPercentage(BigDecimal price, BigDecimal discountPrice) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }
        if (discountPrice == null || discountPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }
        if (discountPrice.compareTo(price) >= 0) {
            // Discount price is not lower than full price — no actual discount
            return 0.0;
        }
        return price.subtract(discountPrice)
                    .divide(price, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
    }
}
