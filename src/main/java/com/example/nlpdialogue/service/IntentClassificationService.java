package com.example.nlpdialogue.service;

import com.example.nlpdialogue.dto.IntentClassificationResponse;
import com.example.nlpdialogue.dto.IntentModelStatusResponse;
import com.example.nlpdialogue.dto.TrainModelResponse;
import jakarta.annotation.PostConstruct;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class IntentClassificationService {

    private final ResourceLoader resourceLoader;
    private final OpenNlpTextProcessingService textProcessingService;
    private final Path modelPath;
    private final String trainingCorpusPath;
    private final boolean autoTrainOnStartup;

    private volatile DoccatModel model;
    private volatile DocumentCategorizerME categorizer;

    public IntentClassificationService(
            ResourceLoader resourceLoader,
            OpenNlpTextProcessingService textProcessingService,
            @Value("${app.opennlp.intent-model-path}") String modelPath,
            @Value("${app.opennlp.training-corpus-path}") String trainingCorpusPath,
            @Value("${app.opennlp.auto-train-on-startup:true}") boolean autoTrainOnStartup) {
        this.resourceLoader = resourceLoader;
        this.textProcessingService = textProcessingService;
        this.modelPath = Path.of(modelPath);
        this.trainingCorpusPath = trainingCorpusPath;
        this.autoTrainOnStartup = autoTrainOnStartup;
    }

    @PostConstruct
    public void initialize() {
        try {
            if (Files.exists(modelPath)) {
                loadModel();
                return;
            }
            if (autoTrainOnStartup) {
                train();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize OpenNLP intent classification model", ex);
        }
    }

    public synchronized TrainModelResponse train() {
        try {
            Resource resource = resourceLoader.getResource("classpath:" + trainingCorpusPath);
            InputStreamFactory inputStreamFactory = () -> resource.getInputStream();

            try (ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
                 ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream)) {

                TrainingParameters parameters = new TrainingParameters();
                parameters.put(TrainingParameters.ITERATIONS_PARAM, "120");
                parameters.put(TrainingParameters.CUTOFF_PARAM, "1");

                DoccatModel trainedModel = DocumentCategorizerME.train(
                        "uk",
                        sampleStream,
                        parameters,
                        new DoccatFactory()
                );

                Files.createDirectories(modelPath.getParent());
                try (OutputStream outputStream = Files.newOutputStream(modelPath)) {
                    trainedModel.serialize(outputStream);
                }

                this.model = trainedModel;
                this.categorizer = new DocumentCategorizerME(trainedModel);

                return new TrainModelResponse(modelPath.toString(), trainingCorpusPath, true,
                        "Intent classification model was trained and saved successfully.");
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot train intent classification model", ex);
        }
    }

    public synchronized void loadModel() throws IOException {
        try (InputStream inputStream = Files.newInputStream(modelPath)) {
            this.model = new DoccatModel(inputStream);
            this.categorizer = new DocumentCategorizerME(this.model);
        }
    }

    public IntentModelStatusResponse status() {
        boolean available = categorizer != null && Files.exists(modelPath);
        return new IntentModelStatusResponse(
                available,
                modelPath.toString(),
                trainingCorpusPath,
                available ? "Intent model is available and ready for classification." : "Intent model is not available. Train the model first."
        );
    }

    public IntentClassificationResponse classify(String text) {
        if (categorizer == null) {
            throw new IllegalStateException("Intent model is not available. Train the model first.");
        }

        String normalizedText = textProcessingService.normalize(text);
        String[] tokens = textProcessingService.tokenize(normalizedText).toArray(String[]::new);
        double[] outcomes = categorizer.categorize(tokens);
        String bestCategory = categorizer.getBestCategory(outcomes);
        double confidence = outcomes[categorizer.getIndex(bestCategory)];

        Map<String, Double> probabilities = new LinkedHashMap<>();
        for (int i = 0; i < categorizer.getNumberOfCategories(); i++) {
            probabilities.put(categorizer.getCategory(i), round(outcomes[i]));
        }

        return new IntentClassificationResponse(bestCategory, round(confidence), probabilities);
    }

    private double round(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}
