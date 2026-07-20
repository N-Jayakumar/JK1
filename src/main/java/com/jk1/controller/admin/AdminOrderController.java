package com.jk1.controller.admin;

import com.jk1.dto.request.AdminOrderUpdateRequestDTO;
import com.jk1.entity.Order;
import com.jk1.entity.enums.OrderStatus;
import com.jk1.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public String listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Page<Order> orderPage = orderService.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        
        model.addAttribute("orderPage", orderPage);
        return "admin/orders";
    }

    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));
        
        model.addAttribute("order", order);
        
        AdminOrderUpdateRequestDTO dto = new AdminOrderUpdateRequestDTO();
        dto.setOrderStatus(order.getOrderStatus());
        dto.setTrackingNumber(order.getTrackingNumber());
        model.addAttribute("updateDto", dto);
        
        return "admin/order-details";
    }

    @PostMapping("/{id}/update-status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @Valid @ModelAttribute("updateDto") AdminOrderUpdateRequestDTO dto,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid status update request.");
            return "redirect:/admin/orders/" + id;
        }

        Order order = orderService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));
        
        order.setOrderStatus(dto.getOrderStatus());
        if (dto.getTrackingNumber() != null && !dto.getTrackingNumber().isBlank()) {
            order.setTrackingNumber(dto.getTrackingNumber());
        }
        
        orderService.save(order);
        redirectAttributes.addFlashAttribute("success", "Order status updated successfully.");
        return "redirect:/admin/orders/" + id;
    }
}
