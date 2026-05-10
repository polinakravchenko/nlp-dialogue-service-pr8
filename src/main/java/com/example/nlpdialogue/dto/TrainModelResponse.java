package com.example.nlpdialogue.dto;

public class TrainModelResponse {
    private String modelPath;
    private String trainingCorpusPath;
    private boolean trained;
    private String message;

    public TrainModelResponse(String modelPath, String trainingCorpusPath, boolean trained, String message) {
        this.modelPath = modelPath;
        this.trainingCorpusPath = trainingCorpusPath;
        this.trained = trained;
        this.message = message;
    }

    public String getModelPath() {
        return modelPath;
    }

    public String getTrainingCorpusPath() {
        return trainingCorpusPath;
    }

    public boolean isTrained() {
        return trained;
    }

    public String getMessage() {
        return message;
    }
}
