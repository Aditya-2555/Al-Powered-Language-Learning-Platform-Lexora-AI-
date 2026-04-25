package com.lingua.repository;

import com.lingua.model.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByUserIdAndLanguageId(Long userId, Long languageId);

    List<UserProgress> findByUserId(Long userId);
}
