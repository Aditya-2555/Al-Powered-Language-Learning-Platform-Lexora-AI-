package com.lingua.repository;

import com.lingua.model.SpeakingAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpeakingAttemptRepository extends JpaRepository<SpeakingAttempt, Long> {
    List<SpeakingAttempt> findByUserIdAndLanguageIdOrderByAttemptedAtDesc(Long userId, Long languageId);
}
