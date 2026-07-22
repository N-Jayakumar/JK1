package com.jk1.service.impl;

import com.jk1.entity.Answer;
import com.jk1.entity.Product;
import com.jk1.entity.Question;
import com.jk1.entity.User;
import com.jk1.repository.AnswerRepository;
import com.jk1.repository.ProductRepository;
import com.jk1.repository.QuestionRepository;
import com.jk1.service.QaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QaServiceImpl implements QaService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Question askQuestion(User user, Long productId, String content) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Question question = Question.builder()
                .product(product)
                .user(user)
                .content(content)
                .build();
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public Answer answerQuestion(User user, Long questionId, String content, boolean isAdmin, boolean isSeller) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        Answer answer = Answer.builder()
                .question(question)
                .user(user)
                .content(content)
                .isAdmin(isAdmin)
                .isSeller(isSeller)
                .isAccepted(false)
                .build();
        return answerRepository.save(answer);
    }

    @Override
    @Transactional
    public void markAcceptedAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found"));
        answer.setAccepted(true);
        answerRepository.save(answer);
    }

    @Override
    public List<Question> getQuestionsForProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return questionRepository.findByProductOrderByCreatedAtDesc(product);
    }
}
