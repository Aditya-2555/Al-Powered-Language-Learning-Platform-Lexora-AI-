package com.lingua.dto;

public class DailyGoalDTO {
    private String goalType;
    private int targetValue;
    private int currentValue;
    private boolean isCompleted;

    public DailyGoalDTO() {}

    public DailyGoalDTO(String goalType, int targetValue, int currentValue, boolean isCompleted) {
        this.goalType = goalType;
        this.targetValue = targetValue;
        this.currentValue = currentValue;
        this.isCompleted = isCompleted;
    }

    public String getGoalType() { return goalType; }
    public void setGoalType(String goalType) { this.goalType = goalType; }
    public int getTargetValue() { return targetValue; }
    public void setTargetValue(int targetValue) { this.targetValue = targetValue; }
    public int getCurrentValue() { return currentValue; }
    public void setCurrentValue(int currentValue) { this.currentValue = currentValue; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}
