package com.lingua.dto;

import java.util.List;

public class ActivityResponseDTO {
    private int xpEarned;
    private int streakBonus;
    private List<AchievementDTO> newlyUnlockedAchievements;

    public ActivityResponseDTO() {}

    public ActivityResponseDTO(int xpEarned, int streakBonus, List<AchievementDTO> newlyUnlockedAchievements) {
        this.xpEarned = xpEarned;
        this.streakBonus = streakBonus;
        this.newlyUnlockedAchievements = newlyUnlockedAchievements;
    }

    public int getXpEarned() { return xpEarned; }
    public void setXpEarned(int xpEarned) { this.xpEarned = xpEarned; }
    public int getStreakBonus() { return streakBonus; }
    public void setStreakBonus(int streakBonus) { this.streakBonus = streakBonus; }
    public List<AchievementDTO> getNewlyUnlockedAchievements() { return newlyUnlockedAchievements; }
    public void setNewlyUnlockedAchievements(List<AchievementDTO> newlyUnlockedAchievements) { this.newlyUnlockedAchievements = newlyUnlockedAchievements; }
}
