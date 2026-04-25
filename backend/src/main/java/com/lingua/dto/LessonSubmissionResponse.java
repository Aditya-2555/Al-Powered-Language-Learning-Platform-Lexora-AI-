package com.lingua.dto;

public class LessonSubmissionResponse {
    private boolean correct;
    private String explanation;
    private String correctAnswer;
    private int xpEarned;

    public LessonSubmissionResponse() {}

    public LessonSubmissionResponse(boolean correct, String explanation, String correctAnswer, int xpEarned) {
        this.correct = correct;
        this.explanation = explanation;
        this.correctAnswer = correctAnswer;
        this.xpEarned = xpEarned;
    }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public int getXpEarned() { return xpEarned; }
    public void setXpEarned(int xpEarned) { this.xpEarned = xpEarned; }
}
