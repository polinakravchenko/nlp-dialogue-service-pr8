package com.example.nlpdialogue.service;

import com.example.nlpdialogue.dto.DialogueMessageResponse;
import com.example.nlpdialogue.dto.DialogueReplyResponse;
import com.example.nlpdialogue.dto.DialogueSessionResponse;
import com.example.nlpdialogue.dto.IntentClassificationResponse;
import com.example.nlpdialogue.entity.ConversationSession;
import com.example.nlpdialogue.entity.DialogueMessage;
import com.example.nlpdialogue.entity.MessageRole;
import com.example.nlpdialogue.exception.ResourceNotFoundException;
import com.example.nlpdialogue.repository.ConversationSessionRepository;
import com.example.nlpdialogue.repository.DialogueMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DialogueService {

    private final ConversationSessionRepository sessionRepository;
    private final DialogueMessageRepository messageRepository;
    private final IntentClassificationService intentClassificationService;
    private final OpenNlpTextProcessingService textProcessingService;
    private final DialogueResponseGenerator responseGenerator;

    public DialogueService(ConversationSessionRepository sessionRepository,
                           DialogueMessageRepository messageRepository,
                           IntentClassificationService intentClassificationService,
                           OpenNlpTextProcessingService textProcessingService,
                           DialogueResponseGenerator responseGenerator) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.intentClassificationService = intentClassificationService;
        this.textProcessingService = textProcessingService;
        this.responseGenerator = responseGenerator;
    }

    @Transactional
    public DialogueSessionResponse startSession(String title) {
        ConversationSession session = new ConversationSession();
        session.setTitle(title == null || title.isBlank() ? "NLP dialogue session" : title);
        ConversationSession saved = sessionRepository.save(session);
        return mapSession(saved, List.of());
    }

    @Transactional
    public DialogueReplyResponse handleMessage(Long sessionId, String userText) {
        ConversationSession session = sessionRepository.findByIdWithMessages(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Dialogue session not found: " + sessionId));

        IntentClassificationResponse intent = intentClassificationService.classify(userText);

        DialogueMessage userMessage = new DialogueMessage();
        userMessage.setRole(MessageRole.USER);
        userMessage.setText(userText);
        userMessage.setNormalizedText(textProcessingService.normalize(userText));
        userMessage.setDetectedIntent(intent.getIntent());
        userMessage.setIntentConfidence(intent.getConfidence());
        session.addMessage(userMessage);

        String assistantReply = responseGenerator.generate(intent.getIntent(), userText);
        DialogueMessage assistantMessage = new DialogueMessage();
        assistantMessage.setRole(MessageRole.ASSISTANT);
        assistantMessage.setText(assistantReply);
        assistantMessage.setNormalizedText(textProcessingService.normalize(assistantReply));
        assistantMessage.setDetectedIntent(intent.getIntent());
        assistantMessage.setIntentConfidence(intent.getConfidence());
        session.addMessage(assistantMessage);

        sessionRepository.save(session);

        List<DialogueMessageResponse> history = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(this::mapMessage)
                .toList();

        return new DialogueReplyResponse(sessionId, userText, assistantReply, intent, history);
    }

    @Transactional(readOnly = true)
    public DialogueSessionResponse getSession(Long sessionId) {
        ConversationSession session = sessionRepository.findByIdWithMessages(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Dialogue session not found: " + sessionId));
        return mapSession(session, session.getMessages().stream().map(this::mapMessage).toList());
    }

    @Transactional(readOnly = true)
    public List<DialogueSessionResponse> getAllSessions() {
        return sessionRepository.findAllWithMessages()
                .stream()
                .map(session -> mapSession(session, session.getMessages().stream().map(this::mapMessage).toList()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DialogueMessageResponse> getHistory(Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("Dialogue session not found: " + sessionId);
        }
        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(this::mapMessage)
                .toList();
    }

    private DialogueSessionResponse mapSession(ConversationSession session, List<DialogueMessageResponse> messages) {
        return new DialogueSessionResponse(
                session.getId(),
                session.getTitle(),
                session.getCreatedAt(),
                session.getUpdatedAt(),
                messages
        );
    }

    private DialogueMessageResponse mapMessage(DialogueMessage message) {
        return new DialogueMessageResponse(
                message.getId(),
                message.getRole(),
                message.getText(),
                message.getDetectedIntent(),
                message.getIntentConfidence(),
                message.getCreatedAt()
        );
    }
}
