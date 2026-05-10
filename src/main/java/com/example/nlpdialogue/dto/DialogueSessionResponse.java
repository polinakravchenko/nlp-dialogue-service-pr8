package com.example.nlpdialogue.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DialogueSessionResponse {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DialogueMessageResponse> messages;

    public DialogueSessionResponse(Long id, String title, LocalDateTime createdAt, LocalDateTime updatedAt, List<DialogueMessageResponse> messages) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.messages = messages;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<DialogueMessageResponse> getMessages() {
        return messages;
    }
}
