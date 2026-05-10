package com.example.nlpdialogue.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OpenNlpTextProcessingServiceTest {

    private final OpenNlpTextProcessingService service = new OpenNlpTextProcessingService();

    @Test
    void normalizeShouldTrimLowercaseAndCollapseSpaces() {
        String result = service.normalize("  Привіт,   NLP СЕРВІС!  ");

        assertThat(result).isEqualTo("привіт, nlp сервіс!");
    }

    @Test
    void tokenizeShouldReturnTokens() {
        List<String> tokens = service.tokenize("hello nlp service");

        assertThat(tokens).containsExactly("hello", "nlp", "service");
    }
}
