package com.example.nlpdialogue.dto.performance;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class PerformanceRequest {
    @NotBlank
    private String text;

    @Min(1)
    private Integer iterations;

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Integer getIterations() { return iterations; }
    public void setIterations(Integer iterations) { this.iterations = iterations; }
}
