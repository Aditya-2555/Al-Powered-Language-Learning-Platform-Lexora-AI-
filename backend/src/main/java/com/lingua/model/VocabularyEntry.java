package com.lingua.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vocabulary_entries")
public class VocabularyEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    private String targetWord;
    private String nativeMeaning;
    private String partOfSpeech;

    @Column(columnDefinition = "TEXT")
    private String exampleTarget;

    @Column(columnDefinition = "TEXT")
    private String exampleNative;

    private String difficulty; // "Beginner", "Intermediate", "Advanced"
    private String source; // "LESSON", "TUTOR", "MANUAL"

    private Boolean isFavorite;
    
    // Spaced Repetition Triggers
    private LocalDateTime lastRevisedAt;
    private LocalDateTime nextRevisionDue;
    private int revisionCount;

    private LocalDateTime createdAt;

    public VocabularyEntry() {
        this.createdAt = LocalDateTime.now();
        this.nextRevisionDue = LocalDateTime.now();
        this.revisionCount = 0;
        this.isFavorite = false;
    }

    public VocabularyEntry(User user, Language language, String targetWord, String nativeMeaning, String partOfSpeech, String exampleTarget, String exampleNative, String difficulty, String source) {
        this.user = user;
        this.language = language;
        this.targetWord = targetWord;
        this.nativeMeaning = nativeMeaning;
        this.partOfSpeech = partOfSpeech;
        this.exampleTarget = exampleTarget;
        this.exampleNative = exampleNative;
        this.difficulty = difficulty != null ? difficulty : "Beginner";
        this.source = source != null ? source : "MANUAL";
        
        this.isFavorite = false;
        this.revisionCount = 0;
        this.createdAt = LocalDateTime.now();
        this.nextRevisionDue = LocalDateTime.now(); // due immediately initially
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }
    public String getTargetWord() { return targetWord; }
    public void setTargetWord(String targetWord) { this.targetWord = targetWord; }
    public String getNativeMeaning() { return nativeMeaning; }
    public void setNativeMeaning(String nativeMeaning) { this.nativeMeaning = nativeMeaning; }
    public String getPartOfSpeech() { return partOfSpeech; }
    public void setPartOfSpeech(String partOfSpeech) { this.partOfSpeech = partOfSpeech; }
    public String getExampleTarget() { return exampleTarget; }
    public void setExampleTarget(String exampleTarget) { this.exampleTarget = exampleTarget; }
    public String getExampleNative() { return exampleNative; }
    public void setExampleNative(String exampleNative) { this.exampleNative = exampleNative; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(Boolean favorite) { isFavorite = favorite; }
    public LocalDateTime getLastRevisedAt() { return lastRevisedAt; }
    public void setLastRevisedAt(LocalDateTime lastRevisedAt) { this.lastRevisedAt = lastRevisedAt; }
    public LocalDateTime getNextRevisionDue() { return nextRevisionDue; }
    public void setNextRevisionDue(LocalDateTime nextRevisionDue) { this.nextRevisionDue = nextRevisionDue; }
    public int getRevisionCount() { return revisionCount; }
    public void setRevisionCount(int revisionCount) { this.revisionCount = revisionCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
