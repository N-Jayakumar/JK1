package com.jk1.controller.admin;

import com.jk1.entity.Review;
import com.jk1.entity.enums.ReviewStatus;
import com.jk1.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public String listReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Page<Review> reviewPage = reviewService.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("pendingReviews", reviewService.getReviewsByStatus(ReviewStatus.PENDING));
        model.addAttribute("reportedReviews", reviewService.getReviewsByStatus(ReviewStatus.REPORTED));
        return "admin/reviews";
    }

    @PostMapping("/{id}/approve")
    public String approveReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.updateReviewStatus(id, ReviewStatus.APPROVED);
        redirectAttributes.addFlashAttribute("success", "Review approved successfully.");
        return "redirect:/admin/reviews";
    }

    @PostMapping("/{id}/reject")
    public String rejectReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.updateReviewStatus(id, ReviewStatus.REJECTED);
        redirectAttributes.addFlashAttribute("success", "Review rejected.");
        return "redirect:/admin/reviews";
    }

    @PostMapping("/{id}/delete")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.deleteReview(id);
        redirectAttributes.addFlashAttribute("success", "Review deleted successfully.");
        return "redirect:/admin/reviews";
    }
}
