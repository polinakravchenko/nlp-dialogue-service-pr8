package com.example.nlpdialogue.service;

import com.example.nlpdialogue.dto.IntentClassificationResponse;
import com.example.nlpdialogue.dto.llm.LlmResponse;
import com.example.nlpdialogue.service.llm.LlmGatewayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LlmGatewayServiceTest {

    @Test
    void generateShouldUseLocalFallbackWhenApiKeyIsMissing() {
        IntentClassificationService intentService = mock(IntentClassificationService.class);
        OpenNlpTextProcessingService textService = mock(OpenNlpTextProcessingService.class);

        when(textService.normalize("classify document")).thenReturn("classify document");
        when(textService.tokenize("classify document")).thenReturn(List.of("classify", "document"));
        when(intentService.classify("classify document"))
                .thenReturn(new IntentClassificationResponse("DOCUMENT_CLASSIFICATION", 0.91, Map.of()));

        LlmGatewayService service = new LlmGatewayService(
                true,
                "openai",
                "https://api.openai.com/v1/responses",
                "gpt-5.4-mini",
                "",
                1,
                1,
                true,
                new ObjectMapper(),
                intentService,
                textService
        );

        LlmResponse response = service.generate("classify document", null);

        assertThat(response.isExternalApiUsed()).isFalse();
        assertThat(response.getStatus()).isEqualTo("API_KEY_NOT_CONFIGURED");
        assertThat(response.getResponseText()).contains("DOCUMENT_CLASSIFICATION");
    }
}
