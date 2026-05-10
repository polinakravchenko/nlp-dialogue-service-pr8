package com.example.nlpdialogue.dto.llm;

public class LlmStatusResponse {
    private final boolean enabled;
    private final String provider;
    private final String model;
    private final boolean apiKeyConfigured;
    private final String apiUrl;
    private final String message;

    public LlmStatusResponse(boolean enabled, String provider, String model, boolean apiKeyConfigured, String apiUrl, String message) {
        this.enabled = enabled;
        this.provider = provider;
        this.model = model;
        this.apiKeyConfigured = apiKeyConfigured;
        this.apiUrl = apiUrl;
        this.message = message;
    }

    public boolean isEnabled() { return enabled; }
    public String getProvider() { return provider; }
    public String getModel() { return model; }
    public boolean isApiKeyConfigured() { return apiKeyConfigured; }
    public String getApiUrl() { return apiUrl; }
    public String getMessage() { return message; }
}
