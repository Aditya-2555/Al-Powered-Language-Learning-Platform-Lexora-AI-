package com.lingua.repository;

import com.lingua.model.DailyGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyGoalRepository extends JpaRepository<DailyGoal, Long> {
    List<DailyGoal> findByUserIdAndLanguageIdAndGoalDate(Long userId, Long languageId, LocalDate goalDate);
}
