package com.jk1.controller;

import com.jk1.dto.request.OrderRequestDTO;
import com.jk1.entity.Order;
import com.jk1.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout/place-order")
    public String placeOrder(@ModelAttribute OrderRequestDTO dto, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        
        try {
            Order order = orderService.placeOrder(principal.getName(), dto);
            return "redirect:/orders/success/" + order.getOrderNumber();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/orders/success/{orderNumber}")
    public String orderSuccess(@PathVariable String orderNumber, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        Order order = orderService.getOrderDetails(principal.getName(), orderNumber);
        model.addAttribute("order", order);
        return "order-success";
    }

    @GetMapping("/account/orders")
    public String myOrders(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        model.addAttribute("orders", orderService.getUserOrders(principal.getName()));
        return "customer/orders";
    }

    @GetMapping("/account/orders/{orderNumber}")
    public String orderDetails(@PathVariable String orderNumber, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        Order order = orderService.getOrderDetails(principal.getName(), orderNumber);
        model.addAttribute("order", order);
        return "customer/order-details";
    }

    @GetMapping("/account/orders/{orderNumber}/track")
    public String trackOrder(@PathVariable String orderNumber, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        Order order = orderService.getOrderDetails(principal.getName(), orderNumber);
        model.addAttribute("order", order);
        return "customer/order-tracking";
    }
}
