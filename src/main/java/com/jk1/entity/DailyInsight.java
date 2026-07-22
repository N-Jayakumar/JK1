package com.jk1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_insights")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyInsight extends BaseAuditEntity {

    @NotBlank
    @Column(name = "insight_text", nullable = false, columnDefinition = "TEXT")
    private String insightText;

    @Column(name = "insight_date", nullable = false)
    private LocalDate insightDate;

    @Column(name = "category", length = 50)
    private String category; // e.g., REVENUE, PRODUCT, CUSTOMER

    @Column(name = "is_positive")
    private Boolean isPositive;
}
