package com.lingua.dto;

import java.time.LocalDateTime;

public class VocabularyDTO {
    private Long id;
    private String targetWord;
    private String nativeMeaning;
    private String partOfSpeech;
    private String exampleTarget;
    private String exampleNative;
    private String difficulty;
    private String source;
    private Boolean isFavorite;
    private LocalDateTime nextRevisionDue;
    private int revisionCount;
    private boolean isDue; // calculated field

    public VocabularyDTO() {}

    public VocabularyDTO(Long id, String targetWord, String nativeMeaning, String partOfSpeech, String exampleTarget, String exampleNative, String difficulty, String source, Boolean isFavorite, LocalDateTime nextRevisionDue, int revisionCount) {
        this.id = id;
        this.targetWord = targetWord;
        this.nativeMeaning = nativeMeaning;
        this.partOfSpeech = partOfSpeech;
        this.exampleTarget = exampleTarget;
        this.exampleNative = exampleNative;
        this.difficulty = difficulty;
        this.source = source;
        this.isFavorite = isFavorite;
        this.nextRevisionDue = nextRevisionDue;
        this.revisionCount = revisionCount;
        this.isDue = nextRevisionDue != null && LocalDateTime.now().isAfter(nextRevisionDue);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }
    public LocalDateTime getNextRevisionDue() { return nextRevisionDue; }
    public void setNextRevisionDue(LocalDateTime nextRevisionDue) { this.nextRevisionDue = nextRevisionDue; }
    public int getRevisionCount() { return revisionCount; }
    public void setRevisionCount(int revisionCount) { this.revisionCount = revisionCount; }
    public boolean isDue() { return isDue; }
    public void setDue(boolean due) { isDue = due; }
}
