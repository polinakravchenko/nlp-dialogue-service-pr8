package com.example.nlpdialogue.dto;

import java.util.Map;

public class IntentClassificationResponse {
    private String intent;
    private double confidence;
    private Map<String, Double> probabilities;

    public IntentClassificationResponse(String intent, double confidence, Map<String, Double> probabilities) {
        this.intent = intent;
        this.confidence = confidence;
        this.probabilities = probabilities;
    }

    public String getIntent() {
        return intent;
    }

    public double getConfidence() {
        return confidence;
    }

    public Map<String, Double> getProbabilities() {
        return probabilities;
    }
}
