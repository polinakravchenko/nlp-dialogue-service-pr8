package com.example.nlpdialogue.dto;

import java.util.List;

public class TextProcessResponse {
    private String originalText;
    private String normalizedText;
    private List<String> sentences;
    private List<String> tokens;
    private IntentClassificationResponse intent;

    public TextProcessResponse(String originalText, String normalizedText, List<String> sentences, List<String> tokens, IntentClassificationResponse intent) {
        this.originalText = originalText;
        this.normalizedText = normalizedText;
        this.sentences = sentences;
        this.tokens = tokens;
        this.intent = intent;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getNormalizedText() {
        return normalizedText;
    }

    public List<String> getSentences() {
        return sentences;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public IntentClassificationResponse getIntent() {
        return intent;
    }
}
