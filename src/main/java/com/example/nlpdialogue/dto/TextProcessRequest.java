package com.example.nlpdialogue.dto;

import jakarta.validation.constraints.NotBlank;

public class TextProcessRequest {

    @NotBlank
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
