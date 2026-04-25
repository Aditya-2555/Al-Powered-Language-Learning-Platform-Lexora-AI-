package com.lingua.repository;

import com.lingua.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Achievement findByBadgeKey(String badgeKey);
}
