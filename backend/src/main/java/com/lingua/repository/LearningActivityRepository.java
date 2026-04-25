package com.lingua.repository;

import com.lingua.model.LearningActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningActivityRepository extends JpaRepository<LearningActivity, Long> {
    List<LearningActivity> findByUserIdAndLanguageIdOrderByCreatedAtDesc(Long userId, Long languageId);
    List<LearningActivity> findByUserIdAndLanguageIdAndIsCorrectFalseOrderByCreatedAtDesc(Long userId, Long languageId);
    List<LearningActivity> findByUserIdAndLanguageIdAndIsCorrectFalseAndResolvedFalseOrderByCreatedAtDesc(Long userId, Long languageId);

    @org.springframework.data.jpa.repository.Query("SELECT l.sourceLessonId FROM LearningActivity l WHERE l.user.id = :userId AND l.language.id = :languageId AND l.sourceLessonId IS NOT NULL ORDER BY l.createdAt DESC")
    List<Long> findRecentSourceLessonIds(@org.springframework.data.repository.query.Param("userId") Long userId, @org.springframework.data.repository.query.Param("languageId") Long languageId, org.springframework.data.domain.Pageable pageable);
}
