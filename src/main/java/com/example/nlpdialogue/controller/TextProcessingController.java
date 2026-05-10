package com.example.nlpdialogue.controller;

import com.example.nlpdialogue.dto.TextProcessRequest;
import com.example.nlpdialogue.dto.TextProcessResponse;
import com.example.nlpdialogue.service.TextProcessingFacade;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/text")
public class TextProcessingController {

    private final TextProcessingFacade textProcessingFacade;

    public TextProcessingController(TextProcessingFacade textProcessingFacade) {
        this.textProcessingFacade = textProcessingFacade;
    }

    @PostMapping("/process")
    public TextProcessResponse process(@Valid @RequestBody TextProcessRequest request) {
        return textProcessingFacade.process(request.getText());
    }
}
