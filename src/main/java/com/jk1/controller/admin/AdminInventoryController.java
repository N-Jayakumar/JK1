package com.jk1.controller.admin;

import com.jk1.entity.Inventory;
import com.jk1.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class AdminInventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public String listInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Inventory> inventoryPage = inventoryService.findAll(pageable);
        
        model.addAttribute("inventoryPage", inventoryPage);
        return "admin/inventory";
    }

    @PostMapping("/update")
    public String updateStock(@RequestParam Long productId,
                              @RequestParam Integer quantity,
                              RedirectAttributes redirectAttributes) {
        try {
            inventoryService.updateStock(productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Stock updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/inventory";
    }
}
