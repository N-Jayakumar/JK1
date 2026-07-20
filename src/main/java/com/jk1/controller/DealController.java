package com.jk1.controller;

import com.jk1.dto.response.ProductResponseDTO;
import com.jk1.mapper.ProductMapper;
import com.jk1.repository.specification.ProductSpecification;
import com.jk1.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Handles the public Deals page for JKØ.
 *
 * <p>Deals are all {@code ACTIVE} products (no authentication required).
 * The page is publicly accessible so anonymous shoppers can browse promotions.</p>
 */
@Controller
@RequestMapping("/deals")
@RequiredArgsConstructor
public class DealController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    /**
     * Renders the Deals page with all active discounted products.
     *
     * <p>If the product table is empty, the template renders an empty-state
     * gracefully — no exception is thrown.</p>
     *
     * @return Thymeleaf template path: templates/deals/view.html
     */
    @GetMapping
    public String viewDeals(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "newest") String sort,
            Model model) {

        try {
            Sort sortOrder = switch (sort) {
                case "price_asc"  -> Sort.by(Sort.Direction.ASC,  "price");
                case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
                case "name_asc"   -> Sort.by(Sort.Direction.ASC,  "name");
                case "name_desc"  -> Sort.by(Sort.Direction.DESC, "name");
                default           -> Sort.by(Sort.Direction.DESC, "createdAt");
            };

            PageRequest pageRequest = PageRequest.of(page, 12, sortOrder);

            // Fetch all ACTIVE products — deals page shows all active products
            // (discountPercentage is set in the mapper; filter if > 0 once real
            //  discount data is available)
            Page<ProductResponseDTO> productPage = productService
                    .findAll(ProductSpecification.searchAndFilter(null, null, null, null, null), pageRequest)
                    .map(productMapper::toResponseDTO);

            List<ProductResponseDTO> deals = productPage.getContent();

            model.addAttribute("deals", deals);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("totalItems", productPage.getTotalElements());
            model.addAttribute("currentSort", sort);

        } catch (Exception e) {
            // Graceful degradation: show empty deals page instead of HTTP 500
            model.addAttribute("deals", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0L);
            model.addAttribute("currentSort", sort);
            model.addAttribute("dealsError", "Unable to load deals at this time. Please try again later.");
        }

        return "deals/view";
    }
}
