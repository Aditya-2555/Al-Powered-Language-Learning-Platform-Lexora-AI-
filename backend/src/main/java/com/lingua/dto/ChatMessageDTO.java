package com.lingua.dto;

public class ChatMessageDTO {
    private String role;
    private String content;
    private String translation;
    private String explanation;
    private String correction;

    public ChatMessageDTO() {}

    public ChatMessageDTO(String role, String content, String translation, String explanation, String correction) {
        this.role = role;
        this.content = content;
        this.translation = translation;
        this.explanation = explanation;
        this.correction = correction;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public String getCorrection() { return correction; }
    public void setCorrection(String correction) { this.correction = correction; }
}
