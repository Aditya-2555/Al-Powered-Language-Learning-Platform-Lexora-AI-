package com.lingua.dto;

public class AdvancedChatResponse {
    private String replyInTargetLanguage;
    private String translationInNativeLanguage;
    private String grammarTipInNativeLanguage;
    private String correctionInNativeLanguage;
    private String suggestedReply;

    public AdvancedChatResponse() {}

    public AdvancedChatResponse(String replyInTargetLanguage, String translationInNativeLanguage, String grammarTipInNativeLanguage, String correctionInNativeLanguage, String suggestedReply) {
        this.replyInTargetLanguage = replyInTargetLanguage;
        this.translationInNativeLanguage = translationInNativeLanguage;
        this.grammarTipInNativeLanguage = grammarTipInNativeLanguage;
        this.correctionInNativeLanguage = correctionInNativeLanguage;
        this.suggestedReply = suggestedReply;
    }

    public String getReplyInTargetLanguage() { return replyInTargetLanguage; }
    public void setReplyInTargetLanguage(String replyInTargetLanguage) { this.replyInTargetLanguage = replyInTargetLanguage; }
    
    public String getTranslationInNativeLanguage() { return translationInNativeLanguage; }
    public void setTranslationInNativeLanguage(String translationInNativeLanguage) { this.translationInNativeLanguage = translationInNativeLanguage; }

    public String getGrammarTipInNativeLanguage() { return grammarTipInNativeLanguage; }
    public void setGrammarTipInNativeLanguage(String grammarTipInNativeLanguage) { this.grammarTipInNativeLanguage = grammarTipInNativeLanguage; }

    public String getCorrectionInNativeLanguage() { return correctionInNativeLanguage; }
    public void setCorrectionInNativeLanguage(String correctionInNativeLanguage) { this.correctionInNativeLanguage = correctionInNativeLanguage; }

    public String getSuggestedReply() { return suggestedReply; }
    public void setSuggestedReply(String suggestedReply) { this.suggestedReply = suggestedReply; }
}
