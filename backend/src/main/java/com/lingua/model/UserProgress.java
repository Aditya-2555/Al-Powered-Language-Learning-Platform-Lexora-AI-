package com.lingua.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_progress")
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    private int xp;
    private int level;

    private int currentStreak;
    private java.time.LocalDate lastActivityDate;
    private int lessonsCompleted;
    private int totalExercisesAttempted;
    private int correctExercises;

    public UserProgress() {}

    public UserProgress(User user, Language language, int xp, int level) {
        this.user = user;
        this.language = language;
        this.xp = xp;
        this.level = level;
        this.currentStreak = 0;
        this.lessonsCompleted = 0;
        this.totalExercisesAttempted = 0;
        this.correctExercises = 0;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }
    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public java.time.LocalDate getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(java.time.LocalDate lastActivityDate) { this.lastActivityDate = lastActivityDate; }

    public int getLessonsCompleted() { return lessonsCompleted; }
    public void setLessonsCompleted(int lessonsCompleted) { this.lessonsCompleted = lessonsCompleted; }

    public int getTotalExercisesAttempted() { return totalExercisesAttempted; }
    public void setTotalExercisesAttempted(int totalExercisesAttempted) { this.totalExercisesAttempted = totalExercisesAttempted; }

    public int getCorrectExercises() { return correctExercises; }
    public void setCorrectExercises(int correctExercises) { this.correctExercises = correctExercises; }
}
