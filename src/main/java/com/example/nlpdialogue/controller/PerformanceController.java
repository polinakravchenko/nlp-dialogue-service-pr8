package com.example.nlpdialogue.controller;

import com.example.nlpdialogue.dto.performance.PerformanceRequest;
import com.example.nlpdialogue.dto.performance.PerformanceResultResponse;
import com.example.nlpdialogue.service.performance.PerformanceEvaluationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final PerformanceEvaluationService performanceEvaluationService;

    public PerformanceController(PerformanceEvaluationService performanceEvaluationService) {
        this.performanceEvaluationService = performanceEvaluationService;
    }

    @PostMapping("/nlp")
    public PerformanceResultResponse evaluateNlpPipeline(@Valid @RequestBody PerformanceRequest request) {
        return performanceEvaluationService.evaluateTextPipeline(request.getText(), request.getIterations());
    }
}
