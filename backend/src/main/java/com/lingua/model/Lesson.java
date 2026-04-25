package com.lingua.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    private String type; // Translate, FillInBlanks, MultipleChoice, etc.
    private String difficulty; // Beginner, Intermediate, Advanced
    private String topic; // Greetings, Food, Family, etc.

    // Native language information
    private String instruction;
    @Column(columnDefinition = "TEXT")
    private String explanation;

    // Target language content
    @Column(columnDefinition = "TEXT")
    private String content;
    
    // JSON arrays/objects or fallback strings
    @Column(columnDefinition = "TEXT")
    private String options; 
    private String correctAnswer;

    public Lesson() {}

    public Lesson(Language language, String type, String difficulty, String topic, String instruction, String explanation, String content, String options, String correctAnswer) {
        this.language = language;
        this.type = type;
        this.difficulty = difficulty;
        this.topic = topic;
        this.instruction = instruction;
        this.explanation = explanation;
        this.content = content;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}
