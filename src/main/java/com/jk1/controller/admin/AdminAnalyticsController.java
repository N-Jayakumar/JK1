package com.jk1.controller.admin;

import com.jk1.entity.enums.OrderStatus;
import com.jk1.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final OrderService orderService;

    @GetMapping
    public String analyticsDashboard(Model model) {
        // Dummy data aggregation for charts
        
        // 1. Sales Trend (Last 7 Days)
        Map<String, BigDecimal> salesTrend = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        
        for (int i = 6; i >= 0; i--) {
            salesTrend.put(today.minusDays(i).format(formatter), new BigDecimal(Math.random() * 5000 + 1000)); // Dummy revenue
        }
        model.addAttribute("salesTrendLabels", salesTrend.keySet());
        model.addAttribute("salesTrendData", salesTrend.values());
        
        // 2. Order Status Breakdown
        List<String> statusLabels = new ArrayList<>();
        List<Integer> statusData = new ArrayList<>();
        
        for (OrderStatus status : OrderStatus.values()) {
            statusLabels.add(status.name());
            statusData.add((int) (Math.random() * 50));
        }
        model.addAttribute("statusLabels", statusLabels);
        model.addAttribute("statusData", statusData);
        
        // 3. Revenue
        model.addAttribute("totalRevenue", new BigDecimal("125430.50"));
        model.addAttribute("monthlyRevenue", new BigDecimal("34500.00"));
        model.addAttribute("todayRevenue", new BigDecimal("1250.00"));

        return "admin/analytics";
    }
}
