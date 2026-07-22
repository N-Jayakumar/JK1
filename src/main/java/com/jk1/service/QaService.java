package com.jk1.service;

import com.jk1.entity.Answer;
import com.jk1.entity.Question;
import com.jk1.entity.User;

import java.util.List;

public interface QaService {
    Question askQuestion(User user, Long productId, String content);
    Answer answerQuestion(User user, Long questionId, String content, boolean isAdmin, boolean isSeller);
    void markAcceptedAnswer(Long answerId);
    List<Question> getQuestionsForProduct(Long productId);
}
