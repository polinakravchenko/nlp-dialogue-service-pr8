package com.example.nlpdialogue.dto;

import com.example.nlpdialogue.entity.MessageRole;

import java.time.LocalDateTime;

public class DialogueMessageResponse {
    private Long id;
    private MessageRole role;
    private String text;
    private String detectedIntent;
    private double confidence;
    private LocalDateTime createdAt;

    public DialogueMessageResponse(Long id, MessageRole role, String text, String detectedIntent, double confidence, LocalDateTime createdAt) {
        this.id = id;
        this.role = role;
        this.text = text;
        this.detectedIntent = detectedIntent;
        this.confidence = confidence;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public MessageRole getRole() {
        return role;
    }

    public String getText() {
        return text;
    }

    public String getDetectedIntent() {
        return detectedIntent;
    }

    public double getConfidence() {
        return confidence;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
