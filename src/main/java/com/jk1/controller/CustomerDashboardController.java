package com.jk1.controller;

import com.jk1.service.NotificationService;
import com.jk1.service.OrderService;
import com.jk1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class CustomerDashboardController {

    private final OrderService orderService;
    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        String email = principal.getName();
        
        // Load limited recent orders and unread notifications for dashboard summary
        model.addAttribute("recentOrders", orderService.getUserOrders(email).stream().limit(3).toList());
        model.addAttribute("unreadNotifications", notificationService.getUnreadNotifications(email));
        
        return "customer/dashboard";
    }

    @GetMapping("/notifications")
    public String notifications(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        model.addAttribute("notifications", notificationService.getUserNotifications(principal.getName()));
        return "customer/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationRead(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        notificationService.markAsRead(id, principal.getName());
        return "redirect:/account/notifications";
    }
}
