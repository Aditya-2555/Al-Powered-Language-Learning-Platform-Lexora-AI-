package com.lingua.dto;

import com.lingua.model.LearningActivity;
import java.util.List;

public class DashboardStatsDTO {
    private int totalXp;
    private int currentStreak;
    private int lessonsCompleted;
    private double accuracyPercentage;
    private String strongestTopic;
    private String weakestTopic;
    private int levelProgressPercentage;
    private int currentLevel;
    private String targetLanguage;
    private List<Integer> weeklyActivityPulse;

    private List<LearningActivity> recentActivities;
    private List<LearningActivity> recentMistakes;
    private RecommendedAction recommendedAction;

    public DashboardStatsDTO() {}

    public static class RecommendedAction {
        public String type;
        public String description;

        public RecommendedAction(String type, String description) {
            this.type = type;
            this.description = description;
        }
    }

    // Getters and Setters
    public int getTotalXp() { return totalXp; }
    public void setTotalXp(int totalXp) { this.totalXp = totalXp; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLessonsCompleted() { return lessonsCompleted; }
    public void setLessonsCompleted(int lessonsCompleted) { this.lessonsCompleted = lessonsCompleted; }

    public double getAccuracyPercentage() { return accuracyPercentage; }
    public void setAccuracyPercentage(double accuracyPercentage) { this.accuracyPercentage = accuracyPercentage; }

    public String getStrongestTopic() { return strongestTopic; }
    public void setStrongestTopic(String strongestTopic) { this.strongestTopic = strongestTopic; }

    public String getWeakestTopic() { return weakestTopic; }
    public void setWeakestTopic(String weakestTopic) { this.weakestTopic = weakestTopic; }

    public int getLevelProgressPercentage() { return levelProgressPercentage; }
    public void setLevelProgressPercentage(int levelProgressPercentage) { this.levelProgressPercentage = levelProgressPercentage; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public String getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(String targetLanguage) { this.targetLanguage = targetLanguage; }

    public List<Integer> getWeeklyActivityPulse() { return weeklyActivityPulse; }
    public void setWeeklyActivityPulse(List<Integer> weeklyActivityPulse) { this.weeklyActivityPulse = weeklyActivityPulse; }

    public List<LearningActivity> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<LearningActivity> recentActivities) { this.recentActivities = recentActivities; }

    public List<LearningActivity> getRecentMistakes() { return recentMistakes; }
    public void setRecentMistakes(List<LearningActivity> recentMistakes) { this.recentMistakes = recentMistakes; }

    public RecommendedAction getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(RecommendedAction recommendedAction) { this.recommendedAction = recommendedAction; }
}
