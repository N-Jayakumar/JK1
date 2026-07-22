package com.jk1.service;

import com.jk1.entity.Review;
import com.jk1.entity.User;
import com.jk1.entity.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review save(Review review);
    Optional<Review> findById(Long id);
    List<Review> findAll();
    Page<Review> findAll(Pageable pageable);
    Page<Review> findAll(Specification<Review> spec, Pageable pageable);
    void deleteById(Long id);
    boolean existsById(Long id);

    Review addReview(User user, Long productId, Integer rating, String comment, List<String> imageUrls);
    void deleteReview(Long reviewId);
    void voteHelpful(User user, Long reviewId, boolean isHelpful);
    void reportReview(Long reviewId);
    void updateReviewStatus(Long reviewId, ReviewStatus status);
    Page<Review> getApprovedReviewsForProduct(Long productId, Pageable pageable);
    List<Review> getReviewsByStatus(ReviewStatus status);
}
