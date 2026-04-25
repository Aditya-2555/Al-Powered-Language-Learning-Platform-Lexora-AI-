package com.lingua.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "speaking_attempts")
public class SpeakingAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    @Column(columnDefinition = "TEXT")
    private String targetPhrase;

    @Column(columnDefinition = "TEXT")
    private String transcribedText;

    private int confidenceScore; // 0-100 indicating closeness between target and transcribed
    private LocalDateTime attemptedAt;

    public SpeakingAttempt() {
        this.attemptedAt = LocalDateTime.now();
    }

    public SpeakingAttempt(User user, Language language, String targetPhrase, String transcribedText, int confidenceScore) {
        this.user = user;
        this.language = language;
        this.targetPhrase = targetPhrase;
        this.transcribedText = transcribedText;
        this.confidenceScore = confidenceScore;
        this.attemptedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }
    public String getTargetPhrase() { return targetPhrase; }
    public void setTargetPhrase(String targetPhrase) { this.targetPhrase = targetPhrase; }
    public String getTranscribedText() { return transcribedText; }
    public void setTranscribedText(String transcribedText) { this.transcribedText = transcribedText; }
    public int getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(int confidenceScore) { this.confidenceScore = confidenceScore; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }
}
