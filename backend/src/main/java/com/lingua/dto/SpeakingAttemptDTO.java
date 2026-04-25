package com.lingua.dto;

import java.time.LocalDateTime;

public class SpeakingAttemptDTO {
    private String targetPhrase;
    private String transcribedText;
    private int confidenceScore;
    private LocalDateTime attemptedAt;

    public SpeakingAttemptDTO() {}

    public SpeakingAttemptDTO(String targetPhrase, String transcribedText, int confidenceScore, LocalDateTime attemptedAt) {
        this.targetPhrase = targetPhrase;
        this.transcribedText = transcribedText;
        this.confidenceScore = confidenceScore;
        this.attemptedAt = attemptedAt;
    }

    public String getTargetPhrase() { return targetPhrase; }
    public void setTargetPhrase(String targetPhrase) { this.targetPhrase = targetPhrase; }
    public String getTranscribedText() { return transcribedText; }
    public void setTranscribedText(String transcribedText) { this.transcribedText = transcribedText; }
    public int getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(int confidenceScore) { this.confidenceScore = confidenceScore; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }
}
