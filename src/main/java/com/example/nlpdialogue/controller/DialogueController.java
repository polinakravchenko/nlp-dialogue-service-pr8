package com.example.nlpdialogue.controller;

import com.example.nlpdialogue.dto.DialogueMessageRequest;
import com.example.nlpdialogue.dto.DialogueMessageResponse;
import com.example.nlpdialogue.dto.DialogueReplyResponse;
import com.example.nlpdialogue.dto.DialogueSessionResponse;
import com.example.nlpdialogue.dto.StartDialogueRequest;
import com.example.nlpdialogue.service.DialogueService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dialogue")
public class DialogueController {

    private final DialogueService dialogueService;

    public DialogueController(DialogueService dialogueService) {
        this.dialogueService = dialogueService;
    }

    @PostMapping("/sessions")
    public DialogueSessionResponse startSession(@RequestBody(required = false) StartDialogueRequest request) {
        String title = request == null ? null : request.getTitle();
        return dialogueService.startSession(title);
    }

    @GetMapping("/sessions")
    public List<DialogueSessionResponse> getAllSessions() {
        return dialogueService.getAllSessions();
    }

    @GetMapping("/sessions/{sessionId}")
    public DialogueSessionResponse getSession(@PathVariable Long sessionId) {
        return dialogueService.getSession(sessionId);
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public DialogueReplyResponse sendMessage(@PathVariable Long sessionId,
                                             @Valid @RequestBody DialogueMessageRequest request) {
        return dialogueService.handleMessage(sessionId, request.getText());
    }

    @GetMapping("/sessions/{sessionId}/history")
    public List<DialogueMessageResponse> getHistory(@PathVariable Long sessionId) {
        return dialogueService.getHistory(sessionId);
    }
}
