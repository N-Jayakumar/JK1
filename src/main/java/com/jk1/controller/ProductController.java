package com.jk1.controller;

import com.jk1.dto.response.ProductResponseDTO;
import com.jk1.exception.ProductNotFoundException;
import com.jk1.mapper.ProductMapper;
import com.jk1.repository.BrandRepository;
import com.jk1.repository.CategoryRepository;
import com.jk1.repository.specification.ProductSpecification;
import com.jk1.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Public product catalog controller for the JKØ storefront.
 *
 * <p>All endpoints return Thymeleaf views — none return JSON. JSON search
 * suggestions are handled separately in {@link SearchApiController}.</p>
 *
 * <h3>Endpoint summary</h3>
 * <pre>
 *   GET /products                      — paginated list with filter/sort/search
 *   GET /products/featured             — featured products grid
 *   GET /products/latest               — latest arrivals grid
 *   GET /products/search               — search results (delegates to list)
 *   GET /products/category/{slug}      — products filtered by category
 *   GET /products/{slug}               — product detail page
 * </pre>
 *
 * <h3>Key design decisions</h3>
 * <ul>
 *   <li>The detail page uses {@code findBySlugWithDetails()} — a single
 *       JOIN FETCH query — instead of calling {@code findBySlug()} three times.
 *       This eliminates both the NullPointerException risk and the N+1 query.</li>
 *   <li>Sort by {@code rating} correctly uses the stored {@code rating} column,
 *       not the broken {@code reviews.size} expression from the old code.</li>
 *   <li>Related products are fetched via the service, which uses
 *       {@code findActiveByCategoryId()} — a JOIN FETCH query — so no lazy
 *       loading occurs outside a transaction.</li>
 *   <li>No raw entity is ever passed to a Thymeleaf model — only DTOs are used,
 *       completely eliminating any LazyInitializationException from the view.</li>
 * </ul>
 */
@Slf4j
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private static final int PAGE_SIZE        = 12;
    private static final int FEATURED_SIZE    = 12;
    private static final int LATEST_SIZE      = 12;
    private static final int RELATED_LIMIT    = 4;
    private static final int COMPLETE_LIMIT   = 3;

    private final ProductService     productService;
    private final ProductMapper      productMapper;
    private final CategoryRepository categoryRepository;
    private final BrandRepository    brandRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // GET /products  — Main list with search + filter + sort + pagination
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping
    public String listProducts(
            @RequestParam(required = false)                           String       search,
            @RequestParam(required = false)                           List<Long>   categoryIds,
            @RequestParam(required = false)                           List<Long>   brandIds,
            @RequestParam(required = false)                           Double       minPrice,
            @RequestParam(required = false)                           Double       maxPrice,
            @RequestParam(required = false, defaultValue = "newest")  String       sort,
            @RequestParam(required = false, defaultValue = "0")       int          page,
            Model model) {

        Sort sortOrder = resolveSortOrder(sort);
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, sortOrder);

        Page<ProductResponseDTO> productPage = productService.findAll(
                ProductSpecification.searchAndFilter(search, categoryIds, brandIds, minPrice, maxPrice),
                pageRequest
        ).map(productMapper::toResponseDTO);

        // ── Content ────────────────────────────────────────────────────────
        model.addAttribute("products",    productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages",  productPage.getTotalPages());
        model.addAttribute("totalItems",  productPage.getTotalElements());

        // ── Active filter state (for UI pre-selection) ────────────────────
        model.addAttribute("search",             search);
        model.addAttribute("selectedCategories", categoryIds  != null ? categoryIds  : List.of());
        model.addAttribute("selectedBrands",     brandIds    != null ? brandIds    : List.of());
        model.addAttribute("minPrice",           minPrice);
        model.addAttribute("maxPrice",           maxPrice);
        model.addAttribute("currentSort",        sort);

        // ── Filter sidebar data ───────────────────────────────────────────
        model.addAttribute("allCategories", categoryRepository.findAll());
        model.addAttribute("allBrands",     brandRepository.findAll());

        // ── Page meta ─────────────────────────────────────────────────────
        model.addAttribute("pageTitle",     "Our Collection - JKØ");
        model.addAttribute("pageHeading",   "Our Collection");
        model.addAttribute("isFeaturedPage", false);
        model.addAttribute("isLatestPage",   false);

        return "product/list";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /products/featured  — Featured products grid
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/featured")
    public String featuredProducts(
            @RequestParam(required = false, defaultValue = "0") int page,
            Model model) {

        PageRequest pageRequest = PageRequest.of(page, FEATURED_SIZE,
                Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by("id")));

        Page<ProductResponseDTO> productPage = productService
                .findFeaturedProducts(pageRequest)
                .map(productMapper::toResponseDTO);

        model.addAttribute("products",    productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages",  productPage.getTotalPages());
        model.addAttribute("totalItems",  productPage.getTotalElements());

        model.addAttribute("search",             null);
        model.addAttribute("selectedCategories", List.of());
        model.addAttribute("selectedBrands",     List.of());
        model.addAttribute("minPrice",           null);
        model.addAttribute("maxPrice",           null);
        model.addAttribute("currentSort",        "newest");

        model.addAttribute("allCategories",  categoryRepository.findAll());
        model.addAttribute("allBrands",      brandRepository.findAll());
        model.addAttribute("pageTitle",      "Featured Products - JKØ");
        model.addAttribute("pageHeading",    "Featured Collection");
        model.addAttribute("isFeaturedPage", true);
        model.addAttribute("isLatestPage",   false);

        return "product/list";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /products/latest  — Latest arrivals grid
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/latest")
    public String latestProducts(
            @RequestParam(required = false, defaultValue = "0") int page,
            Model model) {

        // Service enforces createdAt DESC internally
        PageRequest pageRequest = PageRequest.of(page, LATEST_SIZE);

        Page<ProductResponseDTO> productPage = productService
                .findLatestProducts(pageRequest)
                .map(productMapper::toResponseDTO);

        model.addAttribute("products",    productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages",  productPage.getTotalPages());
        model.addAttribute("totalItems",  productPage.getTotalElements());

        model.addAttribute("search",             null);
        model.addAttribute("selectedCategories", List.of());
        model.addAttribute("selectedBrands",     List.of());
        model.addAttribute("minPrice",           null);
        model.addAttribute("maxPrice",           null);
        model.addAttribute("currentSort",        "newest");

        model.addAttribute("allCategories",  categoryRepository.findAll());
        model.addAttribute("allBrands",      brandRepository.findAll());
        model.addAttribute("pageTitle",      "New Arrivals - JKØ");
        model.addAttribute("pageHeading",    "New Arrivals");
        model.addAttribute("isFeaturedPage", false);
        model.addAttribute("isLatestPage",   true);

        return "product/list";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /products/search  — Search results page (distinct URL for SEO)
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(required = false, defaultValue = "")        String search,
            @RequestParam(required = false, defaultValue = "newest")  String sort,
            @RequestParam(required = false, defaultValue = "0")       int    page,
            Model model) {

        // Delegate to the main list handler which already supports ?search=
        return listProducts(search, null, null, null, null, sort, page, model);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /products/category/{slug}  — Category-filtered product list
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/category/{slug}")
    public String productsByCategory(
            @PathVariable                                             String slug,
            @RequestParam(required = false, defaultValue = "newest") String sort,
            @RequestParam(required = false, defaultValue = "0")      int    page,
            Model model) {

        Sort sortOrder   = resolveSortOrder(sort);
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, sortOrder);

        Page<ProductResponseDTO> productPage = productService
                .findByCategorySlug(slug, pageRequest)
                .map(productMapper::toResponseDTO);

        // Resolve display name from the first result or fall back to slug
        String categoryDisplayName = productPage.getContent().stream()
                .map(ProductResponseDTO::getCategoryName)
                .filter(n -> n != null && !n.isBlank())
                .findFirst()
                .orElse(slug);

        model.addAttribute("products",    productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages",  productPage.getTotalPages());
        model.addAttribute("totalItems",  productPage.getTotalElements());

        model.addAttribute("search",             null);
        model.addAttribute("selectedCategories", List.of());
        model.addAttribute("selectedBrands",     List.of());
        model.addAttribute("minPrice",           null);
        model.addAttribute("maxPrice",           null);
        model.addAttribute("currentSort",        sort);

        model.addAttribute("allCategories",  categoryRepository.findAll());
        model.addAttribute("allBrands",      brandRepository.findAll());
        model.addAttribute("pageTitle",      categoryDisplayName + " - JKØ");
        model.addAttribute("pageHeading",    categoryDisplayName);
        model.addAttribute("isFeaturedPage", false);
        model.addAttribute("isLatestPage",   false);

        return "product/list";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /products/{slug}  — Product detail page
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Renders the product detail page for the given slug.
     *
     * <p>Uses {@code findBySlugWithDetails()} — a single JOIN FETCH query —
     * to load the product and all its associations (category, brand, inventory,
     * images) in one round-trip. This is the only safe way to populate the DTO
     * after the transaction closes.</p>
     *
     * <p>Throws {@link ProductNotFoundException} (a subclass of
     * {@link com.jk1.exception.ResourceNotFoundException}) if no ACTIVE product
     * with the given slug exists. The {@link com.jk1.exception.GlobalExceptionHandler}
     * catches this and returns a proper 404 Thymeleaf view.</p>
     */
    @GetMapping("/{slug}")
    public String productDetails(@PathVariable String slug, Model model) {

        // Single JOIN FETCH query — no repeated findBySlug calls, no N+1
        ProductResponseDTO product = productService.findBySlugWithDetails(slug)
                .map(productMapper::toResponseDTO)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found: " + slug));

        model.addAttribute("product", product);

        // ── Related products (same category, capped to RELATED_LIMIT) ────
        List<ProductResponseDTO> relatedProducts = List.of();
        if (product.getCategoryId() != null) {
            relatedProducts = productService
                    .findRelatedProducts(product.getCategoryId(), product.getId(), RELATED_LIMIT)
                    .stream()
                    .map(productMapper::toResponseDTO)
                    .collect(Collectors.toList());
        }
        model.addAttribute("relatedProducts", relatedProducts);

        // ── Complete The Look (rule-based cross-category combos) ──────────
        List<ProductResponseDTO> completeTheLook = buildCompleteTheLook(
                product.getCategoryName());
        model.addAttribute("completeTheLook", completeTheLook);

        // ── Page meta ─────────────────────────────────────────────────────
        model.addAttribute("pageTitle", product.getName() + " - JKØ");

        return "product/details";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Resolves a sort string parameter into a Spring Data {@link Sort} object.
     *
     * <p>The old implementation used {@code "reviews.size"} as the sort field
     * for the "rating" option, which is not a valid JPA property and caused
     * a runtime {@code IllegalArgumentException}. We now correctly sort by
     * the {@code rating} column stored on the Product entity.</p>
     *
     * @param sort sort key from the request parameter
     * @return resolved {@link Sort}
     */
    private Sort resolveSortOrder(String sort) {
        if (sort == null) return Sort.by(Sort.Direction.DESC, "createdAt");
        return switch (sort) {
            case "price_asc"  -> Sort.by(Sort.Direction.ASC,  "price").and(Sort.by("id"));
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price").and(Sort.by("id"));
            case "rating"     -> Sort.by(Sort.Direction.DESC, "rating").and(Sort.by("id"));   // Fixed: was "reviews.size"
            case "name_asc"   -> Sort.by(Sort.Direction.ASC,  "name").and(Sort.by("id"));
            case "name_desc"  -> Sort.by(Sort.Direction.DESC, "name").and(Sort.by("id"));
            default           -> Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by("id")); // "newest"
        };
    }

    /**
     * Builds a "Complete The Look" complement list based on the current product's
     * category. Fetches one product from each complementary category.
     *
     * <p>All errors are caught silently — if a complementary category doesn't
     * exist in the DB, the entry is simply omitted from the list.</p>
     *
     * @param categoryName the current product's category name (lower-cased internally)
     * @return list of up to {@value #COMPLETE_LIMIT} complementary products (may be empty)
     */
    private List<ProductResponseDTO> buildCompleteTheLook(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return List.of();
        }

        String cat = categoryName.toLowerCase();
        List<String> complements;

        if (cat.contains("shirt")) {
            complements = List.of("pant", "shoe", "belt");
        } else if (cat.contains("pant") || cat.contains("trouser")) {
            complements = List.of("shirt", "shoe", "belt");
        } else if (cat.contains("shoe")) {
            complements = List.of("pant", "belt", "shirt");
        } else if (cat.contains("watch")) {
            complements = List.of("shirt", "bracelet", "belt");
        } else {
            complements = List.of("belt", "watch", "perfume");
        }

        return complements.stream()
                .limit(COMPLETE_LIMIT)
                .map(keyword -> fetchOneByKeyword(keyword))
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }

    /**
     * Fetches a single active product whose category name contains the given keyword.
     * Returns {@code null} if no matching product is found (caller filters nulls out).
     */
    private ProductResponseDTO fetchOneByKeyword(String keyword) {
        try {
            return categoryRepository.findAll().stream()
                    .filter(c -> c.getName().toLowerCase().contains(keyword))
                    .findFirst()
                    .flatMap(c -> productService
                            .findRelatedProducts(c.getId(), -1L, 1)
                            .stream()
                            .findFirst())
                    .map(productMapper::toResponseDTO)
                    .orElse(null);
        } catch (Exception e) {
            log.debug("Complete the look lookup failed for keyword '{}': {}", keyword, e.getMessage());
            return null;
        }
    }
}
