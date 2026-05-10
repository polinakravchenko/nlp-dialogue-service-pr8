package com.example.nlpdialogue.dto.llm;

import jakarta.validation.constraints.NotBlank;

public class LlmRequest {
    @NotBlank
    private String prompt;

    private String systemPrompt;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
}
