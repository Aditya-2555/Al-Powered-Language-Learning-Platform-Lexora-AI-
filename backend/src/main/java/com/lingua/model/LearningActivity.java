package com.lingua.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_activity")
public class LearningActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    private String activityType; // "LESSON", "TUTOR_CHAT"
    private String topic; // "Translate", "MultipleChoice", "Chat"
    private Long sourceLessonId;
    
    private Boolean isCorrect;

    @Column(columnDefinition = "TEXT")
    private String content; // what was tested or said

    @Column(columnDefinition = "TEXT")
    private String mistake; // full mistake text or "N/A"

    @Column(columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private Boolean resolved = false;

    private LocalDateTime createdAt;

    public LearningActivity() {
        this.createdAt = LocalDateTime.now();
        this.resolved = false;
    }

    public LearningActivity(User user, Language language, String activityType, String topic, Long sourceLessonId, Boolean isCorrect, String content, String mistake, String correctAnswer, String explanation) {
        this.user = user;
        this.language = language;
        this.activityType = activityType;
        this.topic = topic;
        this.sourceLessonId = sourceLessonId;
        this.isCorrect = isCorrect;
        this.content = content;
        this.mistake = mistake;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.resolved = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public Long getSourceLessonId() { return sourceLessonId; }
    public void setSourceLessonId(Long sourceLessonId) { this.sourceLessonId = sourceLessonId; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean correct) { isCorrect = correct; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMistake() { return mistake; }
    public void setMistake(String mistake) { this.mistake = mistake; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public Boolean getResolved() { return resolved; }
    public void setResolved(Boolean resolved) { this.resolved = resolved; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
