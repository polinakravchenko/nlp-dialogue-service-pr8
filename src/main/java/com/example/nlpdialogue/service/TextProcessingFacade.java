package com.example.nlpdialogue.service;

import com.example.nlpdialogue.dto.IntentClassificationResponse;
import com.example.nlpdialogue.dto.TextProcessResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TextProcessingFacade {

    private final OpenNlpTextProcessingService textProcessingService;
    private final IntentClassificationService intentClassificationService;

    public TextProcessingFacade(OpenNlpTextProcessingService textProcessingService,
                                IntentClassificationService intentClassificationService) {
        this.textProcessingService = textProcessingService;
        this.intentClassificationService = intentClassificationService;
    }

    public TextProcessResponse process(String text) {
        String normalized = textProcessingService.normalize(text);
        List<String> sentences = textProcessingService.splitSentences(text);
        List<String> tokens = textProcessingService.tokenize(normalized);
        IntentClassificationResponse intent = intentClassificationService.classify(text);
        return new TextProcessResponse(text, normalized, sentences, tokens, intent);
    }
}
