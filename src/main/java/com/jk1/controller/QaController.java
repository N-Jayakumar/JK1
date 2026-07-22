package com.jk1.controller;

import com.jk1.entity.User;
import com.jk1.repository.UserRepository;
import com.jk1.service.QaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
public class QaController {

    private final QaService qaService;
    private final UserRepository userRepository;

    @PostMapping("/questions")
    public ResponseEntity<?> askQuestion(@RequestParam("productId") Long productId,
                                         @RequestParam("content") String content,
                                         Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Must be logged in to ask a question"));
        }
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        qaService.askQuestion(user, productId, content);
        return ResponseEntity.ok(Map.of("message", "Question submitted successfully"));
    }

    @PostMapping("/questions/{id}/answers")
    public ResponseEntity<?> answerQuestion(@PathVariable Long id,
                                            @RequestParam("content") String content,
                                            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Must be logged in to answer"));
        }
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        
        // Mock role checks
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isSeller = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SELLER"));
        
        qaService.answerQuestion(user, id, content, isAdmin, isSeller);
        return ResponseEntity.ok(Map.of("message", "Answer submitted successfully"));
    }

    @PostMapping("/answers/{id}/accept")
    public ResponseEntity<?> acceptAnswer(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Must be logged in"));
        }
        // Ideally check if user owns the question
        qaService.markAcceptedAnswer(id);
        return ResponseEntity.ok(Map.of("message", "Answer accepted"));
    }
}
