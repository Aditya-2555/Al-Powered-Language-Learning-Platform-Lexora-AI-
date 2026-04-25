package com.lingua.model;

import jakarta.persistence.*;

@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String badgeKey; // e.g., "STREAK_3", "XP_100"

    private String title;
    private String description;
    private String iconIdentifier; // e.g., "Award", "Star", "Flame"

    // Optional conditions for dynamic logic mapping
    private Integer targetXp;
    private Integer targetStreak;
    private String requiredTopic; // e.g. "Grammar"
    private Integer targetLessons;

    public Achievement() {}

    public Achievement(String badgeKey, String title, String description, String iconIdentifier, Integer targetXp, Integer targetStreak, String requiredTopic, Integer targetLessons) {
        this.badgeKey = badgeKey;
        this.title = title;
        this.description = description;
        this.iconIdentifier = iconIdentifier;
        this.targetXp = targetXp;
        this.targetStreak = targetStreak;
        this.requiredTopic = requiredTopic;
        this.targetLessons = targetLessons;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBadgeKey() { return badgeKey; }
    public void setBadgeKey(String badgeKey) { this.badgeKey = badgeKey; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIconIdentifier() { return iconIdentifier; }
    public void setIconIdentifier(String iconIdentifier) { this.iconIdentifier = iconIdentifier; }
    public Integer getTargetXp() { return targetXp; }
    public void setTargetXp(Integer targetXp) { this.targetXp = targetXp; }
    public Integer getTargetStreak() { return targetStreak; }
    public void setTargetStreak(Integer targetStreak) { this.targetStreak = targetStreak; }
    public String getRequiredTopic() { return requiredTopic; }
    public void setRequiredTopic(String requiredTopic) { this.requiredTopic = requiredTopic; }
    public Integer getTargetLessons() { return targetLessons; }
    public void setTargetLessons(Integer targetLessons) { this.targetLessons = targetLessons; }
}
