package com.jk1.repository;

import com.jk1.entity.Product;
import com.jk1.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByProductOrderByCreatedAtDesc(Product product);
}
