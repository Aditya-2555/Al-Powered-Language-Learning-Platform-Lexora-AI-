package com.lingua.repository;

import com.lingua.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByLanguageId(Long languageId);
    List<Lesson> findByLanguageIdAndDifficultyAndTopic(Long languageId, String difficulty, String topic);
    List<Lesson> findByLanguageIdAndDifficulty(Long languageId, String difficulty);
}
