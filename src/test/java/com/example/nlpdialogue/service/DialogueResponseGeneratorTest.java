package com.example.nlpdialogue.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DialogueResponseGeneratorTest {

    private final DialogueResponseGenerator generator = new DialogueResponseGenerator();

    @Test
    void generateShouldReturnGreetingResponse() {
        String response = generator.generate("GREETING", "Привіт");

        assertThat(response).contains("NLP-сервіс");
    }

    @Test
    void generateShouldReturnFallbackForUnknownIntent() {
        String response = generator.generate("UNKNOWN_INTENT", "test");

        assertThat(response).contains("UNKNOWN_INTENT");
    }
}
