package com.example.nlpdialogue.dto;

public class IntentModelStatusResponse {
    private boolean modelAvailable;
    private String modelPath;
    private String trainingCorpusPath;
    private String message;

    public IntentModelStatusResponse(boolean modelAvailable, String modelPath, String trainingCorpusPath, String message) {
        this.modelAvailable = modelAvailable;
        this.modelPath = modelPath;
        this.trainingCorpusPath = trainingCorpusPath;
        this.message = message;
    }

    public boolean isModelAvailable() {
        return modelAvailable;
    }

    public String getModelPath() {
        return modelPath;
    }

    public String getTrainingCorpusPath() {
        return trainingCorpusPath;
    }

    public String getMessage() {
        return message;
    }
}
