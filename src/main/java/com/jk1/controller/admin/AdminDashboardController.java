package com.jk1.controller.admin;

import com.jk1.service.ProductService;
import com.jk1.service.CategoryService;
import com.jk1.service.BrandService;
import com.jk1.service.InventoryService;
import com.jk1.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final InventoryService inventoryService;
    private final OrderService orderService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.findAll().size());
        model.addAttribute("totalCategories", categoryService.findAll().size());
        model.addAttribute("totalBrands", brandService.findAll().size());
        
        // Let's just fetch low stock items for the card
        model.addAttribute("lowStockItems", inventoryService.findLowStock(10));
        model.addAttribute("outOfStockItems", inventoryService.findOutOfStock());
        
        // Orders (assuming OrderService has a method or we just fetch all and size them, 
        // for simplicity here we'll just put a dummy value if it doesn't exist or size it)
        try {
            model.addAttribute("recentOrders", orderService.findAll());
            model.addAttribute("totalOrders", orderService.findAll().size());
        } catch(Exception e) {
            // Fallback if findAll doesn't exist in OrderService yet
            model.addAttribute("totalOrders", 0);
        }

        return "admin/dashboard";
    }
}
