package com.jk1.entity;

import com.jk1.entity.enums.AnnouncementTarget;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement extends BaseAuditEntity {

    @NotBlank
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @NotBlank
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_audience", nullable = false, length = 50)
    private AnnouncementTarget targetAudience;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "is_published", nullable = false)
    @Builder.Default
    private boolean isPublished = false;

}
