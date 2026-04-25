package com.lingua.controller;

import com.lingua.dto.DashboardStatsDTO;
import com.lingua.service.ProgressService;
import com.lingua.model.UserProgress;
import com.lingua.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://127.0.0.1:5173", "http://127.0.0.1:5174" })
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private com.lingua.service.RecommendationService recommendationService;

    @GetMapping("/recommendation/{userId}/{languageCode}")
    public com.lingua.dto.RecommendationDTO getRecommendation(@PathVariable Long userId, @PathVariable String languageCode) {
        return recommendationService.getNextBestAction(userId, languageCode);
    }

    @GetMapping("/dashboard/{userId}/{languageCode}")
    public DashboardStatsDTO getDashboardStats(@PathVariable Long userId, @PathVariable String languageCode) {
        return progressService.getDashboardStats(userId, languageCode);
    }

    @PostMapping("/activity")
    public com.lingua.dto.ActivityResponseDTO logActivity(@RequestBody Map<String, Object> payload) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        String languageCode = payload.get("languageCode").toString();
        String activityType = payload.get("activityType").toString();
        String topic = payload.get("topic") != null ? payload.get("topic").toString() : null;
        Long sourceLessonId = payload.get("sourceLessonId") != null ? Long.valueOf(payload.get("sourceLessonId").toString()) : null;
        Object isCorrectObj = payload.get("isCorrect");
        Boolean isCorrect = isCorrectObj != null ? Boolean.valueOf(isCorrectObj.toString()) : null;
        String content = payload.get("content") != null ? payload.get("content").toString() : null;
        String mistake = payload.get("mistake") != null ? payload.get("mistake").toString() : null;
        String correctAnswer = payload.get("correctAnswer") != null ? payload.get("correctAnswer").toString() : null;
        String explanation = payload.get("explanation") != null ? payload.get("explanation").toString() : null;

        return progressService.logActivity(userId, languageCode, activityType, topic, sourceLessonId, isCorrect, content, mistake, correctAnswer, explanation);
    }
    
    @Autowired
    private com.lingua.service.AchievementService achievementService;

    @Autowired
    private GoalService goalService;

    // Explicit completion endpoint for lessons counter
    @PostMapping("/lesson-complete/{userId}/{languageCode}")
    public com.lingua.dto.ActivityResponseDTO lessonComplete(@PathVariable Long userId, @PathVariable String languageCode, @RequestParam(defaultValue = "0") int correctCount, @RequestParam(defaultValue = "0") int totalCount) {
        UserProgress progress = progressService.getProgress(userId, languageCode);
        if (progress != null) {
            progress.setLessonsCompleted(progress.getLessonsCompleted() + 1);
            
            // Award completion Bonus XP
            int bonusXp = 20;
            progress.setXp(progress.getXp() + bonusXp);
            
            // Re-evaluate level
            progress.setLevel((progress.getXp() / 100) + 1);
            
            // Check unlocks
            java.util.List<com.lingua.dto.AchievementDTO> unlocks = achievementService.checkAndUnlockAchievements(progress.getUser(), progress);
            
            goalService.incrementGoal(userId, languageCode, "LESSON", 1);
            progressService.addXp(userId, languageCode, bonusXp); // safe save trigger
            return new com.lingua.dto.ActivityResponseDTO(bonusXp, 0, unlocks);
        }
        return new com.lingua.dto.ActivityResponseDTO(0, 0, new java.util.ArrayList<>());
    }

    @GetMapping("/achievements/{userId}")
    public java.util.List<com.lingua.dto.AchievementDTO> getUserAchievements(@PathVariable Long userId) {
        return achievementService.getUserAchievements(userId);
    }

    @GetMapping("/mistakes/{userId}/{languageCode}")
    public java.util.List<com.lingua.model.LearningActivity> getUnresolvedMistakes(@PathVariable Long userId, @PathVariable String languageCode) {
        return progressService.getUnresolvedMistakes(userId, languageCode);
    }

    @GetMapping("/history/{userId}/{languageCode}")
    public java.util.List<com.lingua.model.LearningActivity> getLessonHistory(@PathVariable Long userId, @PathVariable String languageCode) {
        return progressService.getLessonHistory(userId, languageCode);
    }

    @PostMapping("/mistakes/resolve/{activityId}")
    public com.lingua.dto.ActivityResponseDTO resolveMistake(@PathVariable Long activityId) {
        return progressService.resolveMistake(activityId);
    }
}
