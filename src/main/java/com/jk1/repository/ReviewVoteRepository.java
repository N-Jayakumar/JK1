package com.jk1.repository;

import com.jk1.entity.Review;
import com.jk1.entity.ReviewVote;
import com.jk1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Long> {
    Optional<ReviewVote> findByReviewAndUser(Review review, User user);
}
