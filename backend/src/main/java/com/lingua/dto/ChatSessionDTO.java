package com.lingua.dto;

import java.time.LocalDateTime;

public class ChatSessionDTO {
    private Long id;
    private String title;
    private LocalDateTime startedAt;

    public ChatSessionDTO() {}

    public ChatSessionDTO(Long id, String title, LocalDateTime startedAt) {
        this.id = id;
        this.title = title;
        this.startedAt = startedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
}
