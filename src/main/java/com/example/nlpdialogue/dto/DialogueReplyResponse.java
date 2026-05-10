package com.example.nlpdialogue.dto;

import java.util.List;

public class DialogueReplyResponse {
    private Long sessionId;
    private String userMessage;
    private String assistantReply;
    private IntentClassificationResponse intent;
    private List<DialogueMessageResponse> history;

    public DialogueReplyResponse(Long sessionId, String userMessage, String assistantReply, IntentClassificationResponse intent, List<DialogueMessageResponse> history) {
        this.sessionId = sessionId;
        this.userMessage = userMessage;
        this.assistantReply = assistantReply;
        this.intent = intent;
        this.history = history;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getAssistantReply() {
        return assistantReply;
    }

    public IntentClassificationResponse getIntent() {
        return intent;
    }

    public List<DialogueMessageResponse> getHistory() {
        return history;
    }
}
