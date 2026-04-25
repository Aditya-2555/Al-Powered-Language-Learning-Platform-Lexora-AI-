package com.lingua.dto;

public class RecommendationDTO {
    private String type; // "VOCABULARY", "MISTAKES", "LESSON"
    private String title;
    private String description;
    private String difficulty; // null if not a lesson
    private String topic; // explicitly determined weakest topic
    private double confidenceScore;

    public RecommendationDTO() {}

    public RecommendationDTO(String type, String title, String description, String difficulty, String topic, double confidenceScore) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.topic = topic;
        this.confidenceScore = confidenceScore;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
}
