package com.jk1.repository;

import com.jk1.entity.AiChatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiChatLogRepository extends JpaRepository<AiChatLog, Long> {
    
    @Query("SELECT a.userQuery, COUNT(a) as freq FROM AiChatLog a GROUP BY a.userQuery ORDER BY freq DESC")
    List<Object[]> findPopularSearches();
    
    @Query("SELECT a.intentDetected, COUNT(a) as freq FROM AiChatLog a WHERE a.intentDetected IS NOT NULL GROUP BY a.intentDetected ORDER BY freq DESC")
    List<Object[]> findPopularIntents();
}
