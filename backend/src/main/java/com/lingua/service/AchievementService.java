package com.lingua.service;

import com.lingua.dto.AchievementDTO;
import com.lingua.model.Achievement;
import com.lingua.model.User;
import com.lingua.model.UserAchievement;
import com.lingua.model.UserProgress;
import com.lingua.repository.AchievementRepository;
import com.lingua.repository.UserAchievementRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @PostConstruct
    public void seedAchievements() {
        if (achievementRepository.count() == 0) {
            achievementRepository.save(new Achievement("STREAK_3", "Consistency", "Reach a 3-day streak", "Flame", null, 3, null, null));
            achievementRepository.save(new Achievement("STREAK_7", "Unstoppable", "Reach a 7-day streak", "Flame", null, 7, null, null));
            achievementRepository.save(new Achievement("XP_50", "Getting Started", "Earn 50 Total XP", "Star", 50, null, null, null));
            achievementRepository.save(new Achievement("XP_100", "Dedicated Scholar", "Earn 100 Total XP", "Award", 100, null, null, null));
            achievementRepository.save(new Achievement("LESSONS_5", "Beginner Finisher", "Complete 5 distinct lessons", "CheckCircle", null, null, null, 5));
            achievementRepository.save(new Achievement("GRAMMAR_MASTER", "Grammar Master", "Perform well in Grammar exercises", "Book", null, null, "Grammar", null));
            achievementRepository.save(new Achievement("VOCAB_EXPLORER", "Vocabulary Explorer", "Perform well in Translate exercises", "Target", null, null, "Translate", null));
        }
    }

    public List<AchievementDTO> checkAndUnlockAchievements(User user, UserProgress progress) {
        List<AchievementDTO> newUnlocks = new ArrayList<>();
        List<Achievement> allAchievements = achievementRepository.findAll();

        for (Achievement achievement : allAchievements) {
            // Check if already unlocked
            boolean alreadyUnlocked = userAchievementRepository.existsByUserIdAndAchievementId(user.getId(), achievement.getId());
            if (alreadyUnlocked) continue;

            boolean meetsCriteria = false;

            // Evaluate XP
            if (achievement.getTargetXp() != null && progress.getXp() >= achievement.getTargetXp()) {
                meetsCriteria = true;
            }
            // Evaluate Streak
            if (achievement.getTargetStreak() != null && progress.getCurrentStreak() >= achievement.getTargetStreak()) {
                meetsCriteria = true;
            }
            // Evaluate Lessons Completed
            if (achievement.getTargetLessons() != null && progress.getLessonsCompleted() >= achievement.getTargetLessons()) {
                meetsCriteria = true;
            }
            
            // For topics like Grammar/Translate, this simple logic will just grant it if they have done well recently.
            // A more complex check would look into LearningActivity, but we can base it on XP mapping or grant explicitly via log
            
            if (meetsCriteria) {
                userAchievementRepository.save(new UserAchievement(user, achievement));
                newUnlocks.add(new AchievementDTO(achievement.getTitle(), achievement.getDescription(), achievement.getIconIdentifier(), achievement.getBadgeKey()));
            }
        }

        return newUnlocks;
    }

    public List<AchievementDTO> getUserAchievements(Long userId) {
        return userAchievementRepository.findByUserId(userId).stream().map(ua -> 
            new AchievementDTO(ua.getAchievement().getTitle(), ua.getAchievement().getDescription(), ua.getAchievement().getIconIdentifier(), ua.getAchievement().getBadgeKey())
        ).collect(Collectors.toList());
    }

    // Direct topic trigger evaluation
    public void evaluateTopicAchievement(User user, String topic, List<AchievementDTO> updatesList) {
        if (topic == null) return;
        
        Achievement ach = null;
        if (topic.equalsIgnoreCase("Grammar")) {
             ach = achievementRepository.findByBadgeKey("GRAMMAR_MASTER");
        } else if (topic.equalsIgnoreCase("Translate")) {
             ach = achievementRepository.findByBadgeKey("VOCAB_EXPLORER");
        }
        
        if (ach != null && !userAchievementRepository.existsByUserIdAndAchievementId(user.getId(), ach.getId())) {
             userAchievementRepository.save(new UserAchievement(user, ach));
             updatesList.add(new AchievementDTO(ach.getTitle(), ach.getDescription(), ach.getIconIdentifier(), ach.getBadgeKey()));
        }
    }
}
