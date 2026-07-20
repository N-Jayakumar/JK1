package com.jk1.controller.admin;

import com.jk1.dto.request.AdminCouponRequestDTO;
import com.jk1.entity.Coupon;
import com.jk1.service.CouponService;
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
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    @GetMapping
    public String listCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Page<Coupon> couponPage = couponService.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        
        model.addAttribute("couponPage", couponPage);
        return "admin/coupons";
    }

    @GetMapping("/new")
    public String newCouponForm(Model model) {
        model.addAttribute("couponDto", new AdminCouponRequestDTO());
        return "admin/coupon-form";
    }

    @GetMapping("/edit/{id}")
    public String editCouponForm(@PathVariable Long id, Model model) {
        Coupon coupon = couponService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid coupon Id:" + id));
        
        AdminCouponRequestDTO dto = AdminCouponRequestDTO.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .couponType(coupon.getCouponType())
                .discountPercentage(coupon.getDiscountPercentage())
                .discountAmount(coupon.getDiscountAmount())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .minPurchaseAmount(coupon.getMinPurchaseAmount())
                .expiryDate(coupon.getExpiryDate())
                .usageLimit(coupon.getUsageLimit())
                .isActive(coupon.isActive())
                .build();
                
        model.addAttribute("couponDto", dto);
        return "admin/coupon-form";
    }

    @PostMapping("/save")
    public String saveCoupon(@Valid @ModelAttribute("couponDto") AdminCouponRequestDTO dto,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/coupon-form";
        }

        Coupon coupon = new Coupon();
        if (dto.getId() != null) {
            coupon = couponService.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid coupon Id:" + dto.getId()));
        }

        coupon.setCode(dto.getCode());
        coupon.setCouponType(dto.getCouponType());
        coupon.setDiscountPercentage(dto.getDiscountPercentage() != null ? dto.getDiscountPercentage() : 0);
        coupon.setDiscountAmount(dto.getDiscountAmount());
        coupon.setMaxDiscountAmount(dto.getMaxDiscountAmount());
        coupon.setMinPurchaseAmount(dto.getMinPurchaseAmount());
        coupon.setExpiryDate(dto.getExpiryDate());
        coupon.setUsageLimit(dto.getUsageLimit());
        coupon.setActive(dto.isActive());

        couponService.save(coupon);
        redirectAttributes.addFlashAttribute("success", "Coupon saved successfully.");
        return "redirect:/admin/coupons";
    }

    @GetMapping("/delete/{id}")
    public String deleteCoupon(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        couponService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Coupon deleted successfully.");
        return "redirect:/admin/coupons";
    }
    
    @PostMapping("/{id}/toggle")
    public String toggleCoupon(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Coupon coupon = couponService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid coupon Id:" + id));
        
        coupon.setActive(!coupon.isActive());
        couponService.save(coupon);
        redirectAttributes.addFlashAttribute("success", "Coupon status updated.");
        return "redirect:/admin/coupons";
    }
}
