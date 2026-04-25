package com.lingua.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "daily_goals")
public class DailyGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    private String goalType; // "LESSON", "XP", "VOCAB"
    private int targetValue;
    private int currentValue;
    private boolean isCompleted;
    private LocalDate goalDate;

    public DailyGoal() {}

    public DailyGoal(User user, Language language, String goalType, int targetValue, LocalDate goalDate) {
        this.user = user;
        this.language = language;
        this.goalType = goalType;
        this.targetValue = targetValue;
        this.currentValue = 0;
        this.isCompleted = false;
        this.goalDate = goalDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }
    public String getGoalType() { return goalType; }
    public void setGoalType(String goalType) { this.goalType = goalType; }
    public int getTargetValue() { return targetValue; }
    public void setTargetValue(int targetValue) { this.targetValue = targetValue; }
    public int getCurrentValue() { return currentValue; }
    
    public void setCurrentValue(int currentValue) { 
        this.currentValue = currentValue; 
        if (this.currentValue >= this.targetValue) {
            this.currentValue = this.targetValue;
            this.isCompleted = true;
        }
    }
    
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public LocalDate getGoalDate() { return goalDate; }
    public void setGoalDate(LocalDate goalDate) { this.goalDate = goalDate; }
}
