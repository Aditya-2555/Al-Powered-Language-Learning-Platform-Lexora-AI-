package com.lingua.dto;

public class LessonSubmissionRequest {
    private Long lessonId;
    private String submittedAnswer;

    public LessonSubmissionRequest() {}

    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

    public String getSubmittedAnswer() { return submittedAnswer; }
    public void setSubmittedAnswer(String submittedAnswer) { this.submittedAnswer = submittedAnswer; }
}
