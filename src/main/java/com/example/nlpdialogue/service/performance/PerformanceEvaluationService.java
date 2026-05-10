package com.example.nlpdialogue.service.performance;

import com.example.nlpdialogue.dto.IntentClassificationResponse;
import com.example.nlpdialogue.dto.performance.PerformanceResultResponse;
import com.example.nlpdialogue.service.IntentClassificationService;
import com.example.nlpdialogue.service.OpenNlpTextProcessingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PerformanceEvaluationService {

    private final OpenNlpTextProcessingService textProcessingService;
    private final IntentClassificationService intentClassificationService;
    private final int defaultIterations;

    public PerformanceEvaluationService(OpenNlpTextProcessingService textProcessingService,
                                        IntentClassificationService intentClassificationService,
                                        @Value("${app.performance.default-iterations:50}") int defaultIterations) {
        this.textProcessingService = textProcessingService;
        this.intentClassificationService = intentClassificationService;
        this.defaultIterations = defaultIterations;
    }

    public PerformanceResultResponse evaluateTextPipeline(String text, Integer requestedIterations) {
        int iterations = normalizeIterations(requestedIterations);
        long min = Long.MAX_VALUE;
        long max = 0;
        long total = 0;
        int success = 0;
        int failed = 0;
        Map<String, Long> intentDistribution = new LinkedHashMap<>();
        Instant totalStart = Instant.now();

        for (int i = 0; i < iterations; i++) {
            Instant start = Instant.now();
            try {
                String normalized = textProcessingService.normalize(text);
                textProcessingService.splitSentences(normalized);
                textProcessingService.tokenize(normalized);
                IntentClassificationResponse intent = intentClassificationService.classify(text);
                intentDistribution.merge(intent.getIntent(), 1L, Long::sum);
                success++;
            } catch (RuntimeException ex) {
                failed++;
            }
            long duration = Duration.between(start, Instant.now()).toMillis();
            min = Math.min(min, duration);
            max = Math.max(max, duration);
            total += duration;
        }

        long wallClock = Math.max(1, Duration.between(totalStart, Instant.now()).toMillis());
        double average = iterations == 0 ? 0 : round(total / (double) iterations);
        double throughput = round(success / (wallClock / 1000.0));

        return new PerformanceResultResponse(
                "OPENNLP_TEXT_PIPELINE_AND_INTENT_CLASSIFICATION",
                iterations,
                success,
                failed,
                wallClock,
                average,
                min == Long.MAX_VALUE ? 0 : min,
                max,
                throughput,
                intentDistribution,
                LocalDateTime.now()
        );
    }

    private int normalizeIterations(Integer requestedIterations) {
        if (requestedIterations == null || requestedIterations < 1) {
            return defaultIterations;
        }
        return Math.min(requestedIterations, 5000);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
