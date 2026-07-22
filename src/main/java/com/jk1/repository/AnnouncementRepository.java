package com.jk1.repository;

import com.jk1.entity.Announcement;
import com.jk1.entity.enums.AnnouncementTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByTargetAudienceInAndIsPublishedTrueOrderByScheduledDateDesc(List<AnnouncementTarget> targets);
}
