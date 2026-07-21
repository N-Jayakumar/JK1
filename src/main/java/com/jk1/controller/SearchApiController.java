package com.jk1.controller;

import com.jk1.dto.response.AutocompleteResponseDTO;
import com.jk1.dto.response.ProductResponseDTO;
import com.jk1.mapper.ProductMapper;
import com.jk1.repository.specification.ProductSpecification;
import com.jk1.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Public REST controller for instant search, autocomplete, and suggestions.
 *
 * <h3>Endpoints</h3>
 * <pre>
 *   GET /api/search/suggestions?q={keyword}          (primary — required by spec)
 *   GET /api/v1/search/autocomplete?q={keyword}      (legacy alias — backward compat)
 *   GET /api/v1/search?q={keyword}                   (full search results — up to 5)
 * </pre>
 *
 * <p>All three endpoints are declared {@code .permitAll()} in
 * {@link com.jk1.security.SecurityConfig} ({@code /api/v1/search/**} and
 * {@code /api/search/**}) — no authentication is required.</p>
 *
 * <h3>Architecture decisions</h3>
 * <ul>
 *   <li>Delegates to {@link ProductService#findSuggestions(String)} which uses a
 *       scalar JPQL projection query — <strong>no entity loading, no lazy init
 *       risk, zero N+1 queries</strong>.</li>
 *   <li>Returns {@code max-age=5} cache headers so browsers and CDNs can cache
 *       popular queries for 5 seconds, reducing DB load under high traffic without
 *       serving stale results noticeably.</li>
 *   <li>A blank or whitespace-only {@code q} parameter returns an empty list
 *       immediately — no DB round-trip made.</li>
 *   <li>The effective display price sent to the frontend is the discount price
 *       when present, so the user sees the best price in the dropdown.</li>
 * </ul>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchApiController {

    private static final int AUTOCOMPLETE_LIMIT = 8;
    private static final int FULL_SEARCH_LIMIT  = 5;

    private final ProductService productService;
    private final ProductMapper  productMapper;

    // ─────────────────────────────────────────────────────────────────────────
    // PRIMARY: GET /api/search/suggestions
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Live-search suggestions endpoint (spec-required path).
     *
     * <p>Returns at most {@value #AUTOCOMPLETE_LIMIT} {@link AutocompleteResponseDTO}
     * objects. Blank queries return an empty array — the frontend should guard
     * against calling this with an empty string (it does, via the 1-char minimum
     * in the Alpine component).</p>
     *
     * <p>Example calls:</p>
     * <pre>
     *   GET /api/search/suggestions?q=s   → Formal Shirts, Formal Shoes
     *   GET /api/search/suggestions?q=p   → Formal Pants, Perfumes
     *   GET /api/search/suggestions?q=w   → Watches
     *   GET /api/search/suggestions?q=b   → Leather Belts, Bracelets
     * </pre>
     *
     * @param q the search keyword (optional, defaults to empty string)
     * @return JSON array of {@link AutocompleteResponseDTO}, always non-null
     */
    @GetMapping("/api/search/suggestions")
    public ResponseEntity<List<AutocompleteResponseDTO>> suggestions(
            @RequestParam(defaultValue = "") String q) {

        String query = q.trim();
        if (query.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        log.debug("[Search] Suggestions requested for q={}", query);
        List<AutocompleteResponseDTO> results = productService.findSuggestions(query);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.SECONDS))
                .body(results);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LEGACY ALIAS: GET /api/v1/search/autocomplete
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Legacy autocomplete endpoint — kept for backward compatibility with any
     * existing clients. Delegates to the same service method as the primary
     * {@code /api/search/suggestions} endpoint.
     *
     * @param q the search keyword (optional, defaults to empty string)
     * @return JSON array of {@link AutocompleteResponseDTO}, always non-null
     */
    @GetMapping("/api/v1/search/autocomplete")
    public ResponseEntity<List<AutocompleteResponseDTO>> autocomplete(
            @RequestParam(defaultValue = "") String q) {
        return suggestions(q);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FULL SEARCH: GET /api/v1/search
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Full product search endpoint returning up to {@value #FULL_SEARCH_LIMIT}
     * complete {@link ProductResponseDTO} objects. Used by the search-results
     * redirect when the user submits the search form.
     *
     * @param q search keyword (optional, defaults to empty string)
     * @return JSON array of {@link ProductResponseDTO}
     */
    @GetMapping("/api/v1/search")
    public ResponseEntity<List<ProductResponseDTO>> instantSearch(
            @RequestParam(defaultValue = "") String q) {

        String query = q.trim();
        if (query.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        PageRequest pageRequest = PageRequest.of(0, FULL_SEARCH_LIMIT,
                Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by("id")));

        List<ProductResponseDTO> results = productService
                .findAll(ProductSpecification.searchAndFilter(query, null, null, null, null), pageRequest)
                .getContent()
                .stream()
                .map(productMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(results);
    }
}
