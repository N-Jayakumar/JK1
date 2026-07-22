package com.jk1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_chat_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiChatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", length = 100)
    private String userEmail; // Can be null if guest

    @Column(name = "user_query", length = 1000, nullable = false)
    private String userQuery;

    @Column(name = "intent_detected", length = 100)
    private String intentDetected;

    @Column(name = "ai_response", length = 2000)
    private String aiResponse;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
