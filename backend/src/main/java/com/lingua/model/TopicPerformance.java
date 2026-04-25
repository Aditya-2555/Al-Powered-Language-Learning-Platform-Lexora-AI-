package com.lingua.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "topic_performance", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "language_id", "topic"})
})
public class TopicPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    private String topic;

    private int totalAttempts;
    private int correctAttempts;
    
    // Weighted accuracy 0.0 to 1.0 focusing on recent attempts natively
    private double recentAccuracy;

    private LocalDateTime lastPracticed;

    public TopicPerformance() {}

    public TopicPerformance(User user, Language language, String topic) {
        this.user = user;
        this.language = language;
        this.topic = topic;
        this.totalAttempts = 0;
        this.correctAttempts = 0;
        this.recentAccuracy = 0.5; // neutral starting state
        this.lastPracticed = LocalDateTime.now();
    }

    public void recordAttempt(boolean isCorrect) {
        this.totalAttempts++;
        if (isCorrect) this.correctAttempts++;
        
        // Exponential Moving Average for recent accuracy (prioritizes most recent 10-20 questions heavily)
        double currentAttemptScore = isCorrect ? 1.0 : 0.0;
        double alpha = 0.15; // Weight given to the newest attempt
        this.recentAccuracy = (alpha * currentAttemptScore) + ((1 - alpha) * this.recentAccuracy);
        this.lastPracticed = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public int getTotalAttempts() { return totalAttempts; }
    public void setTotalAttempts(int totalAttempts) { this.totalAttempts = totalAttempts; }
    public int getCorrectAttempts() { return correctAttempts; }
    public void setCorrectAttempts(int correctAttempts) { this.correctAttempts = correctAttempts; }
    public double getRecentAccuracy() { return recentAccuracy; }
    public void setRecentAccuracy(double recentAccuracy) { this.recentAccuracy = recentAccuracy; }
    public LocalDateTime getLastPracticed() { return lastPracticed; }
    public void setLastPracticed(LocalDateTime lastPracticed) { this.lastPracticed = lastPracticed; }
}
