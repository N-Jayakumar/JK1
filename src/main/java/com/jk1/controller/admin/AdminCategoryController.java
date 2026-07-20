package com.jk1.controller.admin;

import com.jk1.dto.request.AdminCategoryRequestDTO;
import com.jk1.entity.Category;
import com.jk1.service.CategoryService;
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
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryService.findAll(pageable);
        
        model.addAttribute("categoryPage", categoryPage);
        return "admin/categories";
    }

    @GetMapping("/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("categoryDto", new AdminCategoryRequestDTO());
        model.addAttribute("parentCategories", categoryService.findAll());
        return "admin/category-form";
    }

    @PostMapping("/save")
    public String saveCategory(@Valid @ModelAttribute("categoryDto") AdminCategoryRequestDTO dto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("parentCategories", categoryService.findAll());
            return "admin/category-form";
        }

        try {
            Category category;
            if (dto.getId() != null) {
                category = categoryService.findById(dto.getId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));
            } else {
                category = new Category();
                category.setSlug(dto.getName().toLowerCase().replace(" ", "-")); // simple slug
            }

            category.setName(dto.getName());
            category.setDescription(dto.getDescription());
            
            if (dto.getParentId() != null) {
                Category parent = categoryService.findById(dto.getParentId())
                        .orElse(null);
                category.setParent(parent);
            } else {
                category.setParent(null);
            }

            categoryService.save(category);
            redirectAttributes.addFlashAttribute("success", "Category saved successfully");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("parentCategories", categoryService.findAll());
            return "admin/category-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
                
        AdminCategoryRequestDTO dto = new AdminCategoryRequestDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
        }

        model.addAttribute("categoryDto", dto);
        model.addAttribute("parentCategories", categoryService.findAll());
        return "admin/category-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Category deleted successfully");
        return "redirect:/admin/categories";
    }
}
