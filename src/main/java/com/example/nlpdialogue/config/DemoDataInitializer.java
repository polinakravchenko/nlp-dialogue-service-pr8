package com.example.nlpdialogue.config;

import com.example.nlpdialogue.dto.DialogueSessionResponse;
import com.example.nlpdialogue.service.DialogueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);
    private final DialogueService dialogueService;

    public DemoDataInitializer(DialogueService dialogueService) {
        this.dialogueService = dialogueService;
    }

    @Override
    public void run(String... args) {
        DialogueSessionResponse session = dialogueService.startSession("Demo NLP dialogue");
        dialogueService.handleMessage(session.getId(), "Привіт, допоможи обробити текст документа.");
        dialogueService.handleMessage(session.getId(), "Потрібно визначити сутності в тексті: Іван Петренко, Київ, The Fintech Lab.");
        log.info("Demo dialogue session was initialized: id={}", session.getId());
    }
}
