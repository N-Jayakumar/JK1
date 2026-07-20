package com.jk1.controller.admin;

import com.jk1.dto.request.AdminProductRequestDTO;
import com.jk1.entity.Product;
import com.jk1.entity.Category;
import com.jk1.entity.Brand;
import com.jk1.entity.Inventory;
import com.jk1.service.ProductService;
import com.jk1.service.CategoryService;
import com.jk1.service.BrandService;
import com.jk1.service.InventoryService;
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
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final InventoryService inventoryService;

    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.findAll(pageable);
        
        model.addAttribute("productPage", productPage);
        return "admin/products";
    }

    @GetMapping("/new")
    public String newProductForm(Model model) {
        model.addAttribute("productDto", new AdminProductRequestDTO());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("brands", brandService.findAll());
        return "admin/product-form";
    }

    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("productDto") AdminProductRequestDTO dto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("brands", brandService.findAll());
            return "admin/product-form";
        }

        try {
            Category category = categoryService.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            Brand brand = brandService.findById(dto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found"));

            Product product;
            if (dto.getId() != null) {
                product = productService.findById(dto.getId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));
            } else {
                product = new Product();
                product.setSlug(dto.getName().toLowerCase().replace(" ", "-")); // simple slug
            }

            product.setName(dto.getName());
            product.setSku(dto.getSku());
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            product.setProductStatus(dto.getProductStatus());
            product.setCategory(category);
            product.setBrand(brand);
            product.setAttributes(dto.getAttributes());

            Product savedProduct = productService.save(product);

            if (dto.getId() == null) {
                // Initialize inventory for new product
                Inventory inventory = new Inventory();
                inventory.setProduct(savedProduct);
                inventory.setQuantity(dto.getInitialQuantity() != null ? dto.getInitialQuantity() : 0);
                inventory.setReservedQuantity(0);
                inventoryService.save(inventory);
            }

            redirectAttributes.addFlashAttribute("success", "Product saved successfully");
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("brands", brandService.findAll());
            return "admin/product-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
                
        AdminProductRequestDTO dto = new AdminProductRequestDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setProductStatus(product.getProductStatus());
        dto.setCategoryId(product.getCategory().getId());
        dto.setBrandId(product.getBrand().getId());
        dto.setAttributes(product.getAttributes());
        
        if (product.getInventory() != null) {
            dto.setInitialQuantity(product.getInventory().getQuantity());
        } else {
            dto.setInitialQuantity(0);
        }

        model.addAttribute("productDto", dto);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("brands", brandService.findAll());
        
        return "admin/product-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Product deleted successfully");
        return "redirect:/admin/products";
    }
}
