package com.jk1.service.impl;

import com.jk1.entity.*;
import com.jk1.entity.enums.ReviewStatus;
import com.jk1.repository.*;
import com.jk1.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewVoteRepository reviewVoteRepository;
    private final ProductRepository productRepository;

    @Override
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    public Page<Review> findAll(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    @Override
    public Page<Review> findAll(Specification<Review> spec, Pageable pageable) {
        return reviewRepository.findAll(spec, pageable);
    }

    @Override
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return reviewRepository.existsById(id);
    }

    @Override
    @Transactional
    public Review addReview(User user, Long productId, Integer rating, String comment, List<String> imageUrls) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        boolean isVerified = true; 

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(rating)
                .comment(comment)
                .isVerifiedPurchase(isVerified)
                .reviewStatus(ReviewStatus.APPROVED)
                .build();
                
        if (imageUrls != null) {
            for (String url : imageUrls) {
                ReviewImage img = ReviewImage.builder()
                        .review(review)
                        .imageUrl(url)
                        .build();
                review.getImages().add(img);
            }
        }

        Review savedReview = reviewRepository.save(review);
        updateProductRating(product);
        return savedReview;
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        Product product = review.getProduct();
        reviewRepository.delete(review);
        updateProductRating(product);
    }

    @Override
    @Transactional
    public void voteHelpful(User user, Long reviewId, boolean isHelpful) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        Optional<ReviewVote> existingVote = reviewVoteRepository.findByReviewAndUser(review, user);
        if (existingVote.isPresent()) {
            ReviewVote vote = existingVote.get();
            if (vote.isHelpful() != isHelpful) {
                vote.setHelpful(isHelpful);
                if (isHelpful) review.setHelpfulVotes(review.getHelpfulVotes() + 1);
                else review.setHelpfulVotes(Math.max(0, review.getHelpfulVotes() - 1));
            }
        } else {
            ReviewVote newVote = ReviewVote.builder()
                    .review(review)
                    .user(user)
                    .isHelpful(isHelpful)
                    .build();
            reviewVoteRepository.save(newVote);
            if (isHelpful) {
                review.setHelpfulVotes(review.getHelpfulVotes() + 1);
            }
        }
        reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void reportReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        review.setReviewStatus(ReviewStatus.REPORTED);
        reviewRepository.save(review);
    }
    
    @Override
    @Transactional
    public void updateReviewStatus(Long reviewId, ReviewStatus status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        review.setReviewStatus(status);
        reviewRepository.save(review);
        updateProductRating(review.getProduct());
    }

    @Override
    public Page<Review> getApprovedReviewsForProduct(Long productId, Pageable pageable) {
        return reviewRepository.findByProduct_IdAndReviewStatus(productId, ReviewStatus.APPROVED, pageable);
    }
    
    @Override
    public List<Review> getReviewsByStatus(ReviewStatus status) {
        return reviewRepository.findByReviewStatus(status);
    }

    private void updateProductRating(Product product) {
        List<Review> approvedReviews = reviewRepository.findByProduct_IdAndReviewStatus(product.getId(), ReviewStatus.APPROVED, Pageable.unpaged()).getContent();
        
        if (approvedReviews.isEmpty()) {
            product.setRating(BigDecimal.ZERO);
            product.setReviewCount(0);
        } else {
            double avg = approvedReviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            product.setRating(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
            product.setReviewCount(approvedReviews.size());
        }
        productRepository.save(product);
    }
}
