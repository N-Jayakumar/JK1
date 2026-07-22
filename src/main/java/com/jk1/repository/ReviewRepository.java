package com.jk1.repository;

import com.jk1.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    List<Review> findByProductId(Long productId);
    List<Review> findByUserId(Long userId);
    
    org.springframework.data.domain.Page<Review> findByProduct_IdAndReviewStatus(Long productId, com.jk1.entity.enums.ReviewStatus status, org.springframework.data.domain.Pageable pageable);
    
    List<Review> findByReviewStatus(com.jk1.entity.enums.ReviewStatus status);
}
