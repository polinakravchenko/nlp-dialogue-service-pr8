package com.example.nlpdialogue.dto.performance;

import java.time.LocalDateTime;
import java.util.Map;

public class PerformanceResultResponse {
    private final String operation;
    private final int iterations;
    private final int successfulIterations;
    private final int failedIterations;
    private final long totalDurationMs;
    private final double averageDurationMs;
    private final long minDurationMs;
    private final long maxDurationMs;
    private final double throughputPerSecond;
    private final Map<String, Long> predictedIntentDistribution;
    private final LocalDateTime measuredAt;

    public PerformanceResultResponse(String operation, int iterations, int successfulIterations, int failedIterations,
                                     long totalDurationMs, double averageDurationMs, long minDurationMs, long maxDurationMs,
                                     double throughputPerSecond, Map<String, Long> predictedIntentDistribution,
                                     LocalDateTime measuredAt) {
        this.operation = operation;
        this.iterations = iterations;
        this.successfulIterations = successfulIterations;
        this.failedIterations = failedIterations;
        this.totalDurationMs = totalDurationMs;
        this.averageDurationMs = averageDurationMs;
        this.minDurationMs = minDurationMs;
        this.maxDurationMs = maxDurationMs;
        this.throughputPerSecond = throughputPerSecond;
        this.predictedIntentDistribution = predictedIntentDistribution;
        this.measuredAt = measuredAt;
    }

    public String getOperation() { return operation; }
    public int getIterations() { return iterations; }
    public int getSuccessfulIterations() { return successfulIterations; }
    public int getFailedIterations() { return failedIterations; }
    public long getTotalDurationMs() { return totalDurationMs; }
    public double getAverageDurationMs() { return averageDurationMs; }
    public long getMinDurationMs() { return minDurationMs; }
    public long getMaxDurationMs() { return maxDurationMs; }
    public double getThroughputPerSecond() { return throughputPerSecond; }
    public Map<String, Long> getPredictedIntentDistribution() { return predictedIntentDistribution; }
    public LocalDateTime getMeasuredAt() { return measuredAt; }
}
