package com.lingua.dto;

public class DailyChallengeDTO {
    private Long id;
    private String question;
    private String optionsJson;
    private String correctAnswer;
    private String explanation;
    private boolean isCompleted;

    public DailyChallengeDTO() {}

    public DailyChallengeDTO(Long id, String question, String optionsJson, String correctAnswer, String explanation, boolean isCompleted) {
        this.id = id;
        this.question = question;
        this.optionsJson = optionsJson;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.isCompleted = isCompleted;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getOptionsJson() { return optionsJson; }
    public void setOptionsJson(String optionsJson) { this.optionsJson = optionsJson; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(boolean isCompleted) { this.isCompleted = isCompleted; }
}
