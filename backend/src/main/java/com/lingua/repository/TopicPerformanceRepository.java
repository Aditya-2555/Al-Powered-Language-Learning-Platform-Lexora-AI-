package com.lingua.repository;

import com.lingua.model.TopicPerformance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicPerformanceRepository extends JpaRepository<TopicPerformance, Long> {
    Optional<TopicPerformance> findByUserIdAndLanguageIdAndTopic(Long userId, Long languageId, String topic);
    List<TopicPerformance> findByUserIdAndLanguageId(Long userId, Long languageId);
    List<TopicPerformance> findByUserIdAndLanguageIdOrderByRecentAccuracyAsc(Long userId, Long languageId);
}
