package com.jk1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Slim DTO returned by the autocomplete / suggestions endpoints.
 *
 * <p>Carries only the fields the frontend dropdown needs — intentionally
 * lightweight to minimise JSON payload size and serialisation time.</p>
 *
 * <p>Fields:</p>
 * <ul>
 *   <li>{@code id}           – product primary key (for cart / wishlist calls)</li>
 *   <li>{@code name}         – product display name</li>
 *   <li>{@code slug}         – URL slug → {@code /products/{slug}}</li>
 *   <li>{@code categoryName} – shown as a badge chip in the dropdown</li>
 *   <li>{@code categorySlug} – for category-level breadcrumb links</li>
 *   <li>{@code brandName}    – shown as a secondary label</li>
 *   <li>{@code imageUrl}     – 100 × 100 thumbnail beside the result row</li>
 *   <li>{@code price}        – effective price (discount price if present, else full price)</li>
 *   <li>{@code discountPrice}– raw discount price (null when no discount)</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutocompleteResponseDTO {

    private Long       id;
    private String     name;
    private String     slug;
    private String     categoryName;
    private String     categorySlug;
    private String     brandName;
    private String     imageUrl;
    /** Effective display price: discountPrice when set, otherwise price. */
    private BigDecimal price;
    /** Raw discount price; null when product has no active discount. */
    private BigDecimal discountPrice;
}
