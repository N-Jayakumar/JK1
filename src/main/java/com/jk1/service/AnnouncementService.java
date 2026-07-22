package com.jk1.service;

import com.jk1.entity.Announcement;
import com.jk1.entity.enums.AnnouncementTarget;
import com.jk1.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    public Optional<Announcement> getAnnouncement(Long id) {
        return announcementRepository.findById(id);
    }

    public List<Announcement> getActiveAnnouncementsForTarget(List<AnnouncementTarget> targets) {
        return announcementRepository.findByTargetAudienceInAndIsPublishedTrueOrderByScheduledDateDesc(targets);
    }

    @Transactional
    public Announcement createAnnouncement(String title, String content, AnnouncementTarget target, LocalDateTime scheduledDate) {
        Announcement announcement = Announcement.builder()
                .title(title)
                .content(content)
                .targetAudience(target)
                .scheduledDate(scheduledDate)
                .isPublished(scheduledDate == null || scheduledDate.isBefore(LocalDateTime.now()))
                .build();
        return announcementRepository.save(announcement);
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }
}
