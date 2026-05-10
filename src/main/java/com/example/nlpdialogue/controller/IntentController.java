package com.example.nlpdialogue.controller;

import com.example.nlpdialogue.dto.IntentClassificationResponse;
import com.example.nlpdialogue.dto.IntentModelStatusResponse;
import com.example.nlpdialogue.dto.TextProcessRequest;
import com.example.nlpdialogue.dto.TrainModelResponse;
import com.example.nlpdialogue.service.IntentClassificationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/intent")
public class IntentController {

    private final IntentClassificationService intentClassificationService;

    public IntentController(IntentClassificationService intentClassificationService) {
        this.intentClassificationService = intentClassificationService;
    }

    @GetMapping("/status")
    public IntentModelStatusResponse status() {
        return intentClassificationService.status();
    }

    @PostMapping("/train")
    public TrainModelResponse train() {
        return intentClassificationService.train();
    }

    @PostMapping("/classify")
    public IntentClassificationResponse classify(@Valid @RequestBody TextProcessRequest request) {
        return intentClassificationService.classify(request.getText());
    }
}
