package com.lingua.repository;

import com.lingua.model.VocabularyEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VocabularyRepository extends JpaRepository<VocabularyEntry, Long> {
    List<VocabularyEntry> findByUserIdAndLanguageIdOrderByCreatedAtDesc(Long userId, Long languageId);
    boolean existsByUserIdAndLanguageIdAndTargetWordIgnoreCase(Long userId, Long languageId, String targetWord);
}
