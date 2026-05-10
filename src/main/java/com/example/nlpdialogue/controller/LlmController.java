package com.example.nlpdialogue.controller;

import com.example.nlpdialogue.dto.llm.LlmRequest;
import com.example.nlpdialogue.dto.llm.LlmResponse;
import com.example.nlpdialogue.dto.llm.LlmStatusResponse;
import com.example.nlpdialogue.service.llm.LlmGatewayService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/llm")
public class LlmController {

    private final LlmGatewayService llmGatewayService;

    public LlmController(LlmGatewayService llmGatewayService) {
        this.llmGatewayService = llmGatewayService;
    }

    @GetMapping("/status")
    public LlmStatusResponse status() {
        return llmGatewayService.status();
    }

    @PostMapping("/generate")
    public LlmResponse generate(@Valid @RequestBody LlmRequest request) {
        return llmGatewayService.generate(request.getPrompt(), request.getSystemPrompt());
    }
}
