package com.jk1.controller;

import com.jk1.entity.User;
import com.jk1.repository.UserRepository;
import com.jk1.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> addReview(
            @RequestParam("productId") Long productId,
            @RequestParam("rating") Integer rating,
            @RequestParam("comment") String comment,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            Authentication authentication) {
            
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Must be logged in to review"));
        }
        
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        
        List<String> imageUrls = new ArrayList<>();
        if (images != null) {
            for (MultipartFile file : images) {
                // Mock image upload
                imageUrls.add("/uploads/reviews/mock_" + file.getOriginalFilename());
            }
        }
        
        reviewService.addReview(user, productId, rating, comment, imageUrls);
        return ResponseEntity.ok(Map.of("message", "Review submitted successfully"));
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<?> voteHelpful(@PathVariable Long id, @RequestParam("helpful") boolean isHelpful, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Must be logged in to vote"));
        }
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        reviewService.voteHelpful(user, id, isHelpful);
        return ResponseEntity.ok(Map.of("message", "Vote recorded"));
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<?> reportReview(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Must be logged in to report"));
        }
        reviewService.reportReview(id);
        return ResponseEntity.ok(Map.of("message", "Review reported"));
    }
}
