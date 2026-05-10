package com.example.nlpdialogue.service.llm;

import com.example.nlpdialogue.dto.llm.LlmResponse;
import com.example.nlpdialogue.dto.llm.LlmStatusResponse;
import com.example.nlpdialogue.service.IntentClassificationService;
import com.example.nlpdialogue.service.OpenNlpTextProcessingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class LlmGatewayService {

    private final boolean enabled;
    private final String provider;
    private final String apiUrl;
    private final String model;
    private final String apiKey;
    private final boolean fallbackToLocalResponse;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final IntentClassificationService intentClassificationService;
    private final OpenNlpTextProcessingService textProcessingService;
    private final int requestTimeoutSeconds;

    public LlmGatewayService(
            @Value("${app.llm.enabled:true}") boolean enabled,
            @Value("${app.llm.provider:openai}") String provider,
            @Value("${app.llm.api-url:https://api.openai.com/v1/responses}") String apiUrl,
            @Value("${app.llm.model:gpt-5.4-mini}") String model,
            @Value("${app.llm.api-key:}") String apiKey,
            @Value("${app.llm.connect-timeout-seconds:10}") int connectTimeoutSeconds,
            @Value("${app.llm.request-timeout-seconds:60}") int requestTimeoutSeconds,
            @Value("${app.llm.fallback-to-local-response:true}") boolean fallbackToLocalResponse,
            ObjectMapper objectMapper,
            IntentClassificationService intentClassificationService,
            OpenNlpTextProcessingService textProcessingService) {
        this.enabled = enabled;
        this.provider = provider;
        this.apiUrl = apiUrl;
        this.model = model;
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.fallbackToLocalResponse = fallbackToLocalResponse;
        this.objectMapper = objectMapper;
        this.intentClassificationService = intentClassificationService;
        this.textProcessingService = textProcessingService;
        this.requestTimeoutSeconds = requestTimeoutSeconds;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(connectTimeoutSeconds))
                .build();
    }

    public LlmStatusResponse status() {
        boolean keyConfigured = apiKey != null && !apiKey.isBlank();
        String message;
        if (!enabled) {
            message = "LLM integration is disabled by configuration.";
        } else if (!keyConfigured) {
            message = "LLM integration is enabled, but API key is not configured. Local fallback response will be used if enabled.";
        } else {
            message = "LLM integration is configured and ready.";
        }
        return new LlmStatusResponse(enabled, provider, model, keyConfigured, apiUrl, message);
    }

    public LlmResponse generate(String prompt, String systemPrompt) {
        Instant start = Instant.now();
        if (!enabled) {
            return fallback(prompt, start, "DISABLED");
        }
        if (apiKey == null || apiKey.isBlank()) {
            return fallback(prompt, start, "API_KEY_NOT_CONFIGURED");
        }

        try {
            String requestBody = buildResponsesApiRequest(prompt, systemPrompt);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(requestTimeoutSeconds))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long duration = Duration.between(start, Instant.now()).toMillis();
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return new LlmResponse(provider, model, prompt, extractText(response.body()), true,
                        "OK", duration, LocalDateTime.now());
            }
            if (fallbackToLocalResponse) {
                return fallback(prompt, start, "HTTP_" + response.statusCode());
            }
            throw new IllegalStateException("LLM API returned HTTP " + response.statusCode() + ": " + response.body());
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            if (fallbackToLocalResponse) {
                return fallback(prompt, start, ex.getClass().getSimpleName());
            }
            throw new IllegalStateException("Cannot call LLM API", ex);
        }
    }

    private String buildResponsesApiRequest(String prompt, String systemPrompt) throws IOException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", model);
        payload.put("input", List.of(
                Map.of("role", "system", "content", systemPrompt == null || systemPrompt.isBlank()
                        ? "You are an NLP assistant. Answer briefly and clearly."
                        : systemPrompt),
                Map.of("role", "user", "content", prompt)
        ));
        return objectMapper.writeValueAsString(payload);
    }

    private String extractText(String body) throws IOException {
        JsonNode root = objectMapper.readTree(body);
        JsonNode outputText = root.get("output_text");
        if (outputText != null && outputText.isTextual()) {
            return outputText.asText();
        }
        JsonNode output = root.get("output");
        if (output != null && output.isArray()) {
            StringBuilder sb = new StringBuilder();
            for (JsonNode item : output) {
                JsonNode content = item.get("content");
                if (content != null && content.isArray()) {
                    for (JsonNode contentItem : content) {
                        JsonNode text = contentItem.get("text");
                        if (text != null && text.isTextual()) {
                            if (!sb.isEmpty()) {
                                sb.append('\n');
                            }
                            sb.append(text.asText());
                        }
                    }
                }
            }
            if (!sb.isEmpty()) {
                return sb.toString();
            }
        }
        return body;
    }

    private LlmResponse fallback(String prompt, Instant start, String status) {
        if (!fallbackToLocalResponse) {
            throw new IllegalStateException("LLM API is unavailable and fallback is disabled. Status: " + status);
        }
        String normalized = textProcessingService.normalize(prompt);
        List<String> tokens = textProcessingService.tokenize(normalized);
        String intent = "UNKNOWN";
        try {
            intent = intentClassificationService.classify(prompt).getIntent();
        } catch (RuntimeException ignored) {
            // The fallback must remain available even if the local OpenNLP model is not loaded.
        }
        String text = "Local fallback response. Detected intent: " + intent
                + ". Token count: " + tokens.size()
                + ". Normalized text: " + normalized;
        long duration = Duration.between(start, Instant.now()).toMillis();
        return new LlmResponse(provider, model, prompt, text, false, status, duration, LocalDateTime.now());
    }
}
