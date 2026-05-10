package com.example.nlpdialogue.dto.llm;

import java.time.LocalDateTime;

public class LlmResponse {
    private final String provider;
    private final String model;
    private final String prompt;
    private final String responseText;
    private final boolean externalApiUsed;
    private final String status;
    private final long durationMs;
    private final LocalDateTime createdAt;

    public LlmResponse(String provider, String model, String prompt, String responseText,
                       boolean externalApiUsed, String status, long durationMs, LocalDateTime createdAt) {
        this.provider = provider;
        this.model = model;
        this.prompt = prompt;
        this.responseText = responseText;
        this.externalApiUsed = externalApiUsed;
        this.status = status;
        this.durationMs = durationMs;
        this.createdAt = createdAt;
    }

    public String getProvider() { return provider; }
    public String getModel() { return model; }
    public String getPrompt() { return prompt; }
    public String getResponseText() { return responseText; }
    public boolean isExternalApiUsed() { return externalApiUsed; }
    public String getStatus() { return status; }
    public long getDurationMs() { return durationMs; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
