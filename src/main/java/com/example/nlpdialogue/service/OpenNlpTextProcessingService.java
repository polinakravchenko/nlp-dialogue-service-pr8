package com.example.nlpdialogue.service;

import opennlp.tools.tokenize.SimpleTokenizer;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class OpenNlpTextProcessingService {

    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");
    private static final Pattern SENTENCE_SPLIT_PATTERN = Pattern.compile("(?<=[.!?])\\s+");

    public String normalize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKC);
        normalized = normalized.trim().toLowerCase(Locale.ROOT);
        normalized = normalized.replaceAll("[\\p{Cntrl}]", " ");
        return MULTIPLE_SPACES.matcher(normalized).replaceAll(" ");
    }

    public List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return Arrays.asList(SimpleTokenizer.INSTANCE.tokenize(text));
    }

    public List<String> splitSentences(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return Arrays.stream(SENTENCE_SPLIT_PATTERN.split(text.trim()))
                .filter(sentence -> !sentence.isBlank())
                .toList();
    }
}
