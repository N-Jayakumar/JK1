package com.jk1.controller.admin;

import com.jk1.dto.request.AdminBrandRequestDTO;
import com.jk1.entity.Brand;
import com.jk1.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/brands")
@RequiredArgsConstructor
public class AdminBrandController {

    private final BrandService brandService;

    @GetMapping
    public String listBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Brand> brandPage = brandService.findAll(pageable);
        
        model.addAttribute("brandPage", brandPage);
        return "admin/brands";
    }

    @GetMapping("/new")
    public String newBrandForm(Model model) {
        model.addAttribute("brandDto", new AdminBrandRequestDTO());
        return "admin/brand-form";
    }

    @PostMapping("/save")
    public String saveBrand(@Valid @ModelAttribute("brandDto") AdminBrandRequestDTO dto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "admin/brand-form";
        }

        try {
            Brand brand;
            if (dto.getId() != null) {
                brand = brandService.findById(dto.getId())
                        .orElseThrow(() -> new RuntimeException("Brand not found"));
            } else {
                brand = new Brand();
            }

            brand.setName(dto.getName());
            brand.setDescription(dto.getDescription());
            brand.setLogoUrl(dto.getLogoUrl());

            brandService.save(brand);
            redirectAttributes.addFlashAttribute("success", "Brand saved successfully");
            return "redirect:/admin/brands";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/brand-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editBrandForm(@PathVariable Long id, Model model) {
        Brand brand = brandService.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
                
        AdminBrandRequestDTO dto = new AdminBrandRequestDTO();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setDescription(brand.getDescription());
        dto.setLogoUrl(brand.getLogoUrl());

        model.addAttribute("brandDto", dto);
        return "admin/brand-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        brandService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Brand deleted successfully");
        return "redirect:/admin/brands";
    }
}
