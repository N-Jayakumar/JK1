package com.jk1.controller.admin;

import com.jk1.entity.User;
import com.jk1.entity.enums.AccountStatus;
import com.jk1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final UserService userService;

    @GetMapping
    public String listCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Page<User> customerPage = userService.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        
        model.addAttribute("customerPage", customerPage);
        return "admin/customers";
    }

    @GetMapping("/{id}")
    public String customerDetails(@PathVariable Long id, Model model) {
        User customer = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer Id:" + id));
        
        model.addAttribute("customer", customer);
        return "admin/customer-details";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleCustomerStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User customer = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer Id:" + id));
        
        if (customer.getAccountStatus() == AccountStatus.ACTIVE) {
            customer.setAccountStatus(AccountStatus.SUSPENDED);
            redirectAttributes.addFlashAttribute("success", "Customer account locked.");
        } else {
            customer.setAccountStatus(AccountStatus.ACTIVE);
            redirectAttributes.addFlashAttribute("success", "Customer account activated.");
        }
        
        userService.save(customer);
        return "redirect:/admin/customers/" + id;
    }
}
