package com.jk1.controller;

import com.jk1.service.ProductService;
import com.jk1.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.stream.Collectors;

/**
 * Handles the public home page of JKØ.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        // Fetch 4 featured products
        var featured = productService.findFeaturedProducts(PageRequest.of(0, 4))
                .getContent().stream().map(productMapper::toResponseDTO).collect(Collectors.toList());
        
        // Fetch 4 best sellers
        var bestSellers = productService.findBestSellers(PageRequest.of(0, 4))
                .getContent().stream().map(productMapper::toResponseDTO).collect(Collectors.toList());
                
        // Fetch 8 latest arrivals
        var newArrivals = productService.findLatestProducts(PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent().stream().map(productMapper::toResponseDTO).collect(Collectors.toList());
        
        // We will just use newArrivals as latestProducts if needed, or pass it twice.
        model.addAttribute("featuredProducts", featured);
        model.addAttribute("bestSellers", bestSellers);
        model.addAttribute("newArrivals", newArrivals);
        model.addAttribute("latestProducts", newArrivals);

        return "home/index";
    }
}
