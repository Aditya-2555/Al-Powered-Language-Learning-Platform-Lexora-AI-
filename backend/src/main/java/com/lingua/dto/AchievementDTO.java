package com.lingua.dto;

public class AchievementDTO {
    private String title;
    private String description;
    private String iconIdentifier;
    private String badgeKey;

    public AchievementDTO(String title, String description, String iconIdentifier, String badgeKey) {
        this.title = title;
        this.description = description;
        this.iconIdentifier = iconIdentifier;
        this.badgeKey = badgeKey;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIconIdentifier() { return iconIdentifier; }
    public void setIconIdentifier(String iconIdentifier) { this.iconIdentifier = iconIdentifier; }
    public String getBadgeKey() { return badgeKey; }
    public void setBadgeKey(String badgeKey) { this.badgeKey = badgeKey; }
}
