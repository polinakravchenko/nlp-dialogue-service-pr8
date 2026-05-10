package com.example.nlpdialogue.repository;

import com.example.nlpdialogue.entity.ConversationSession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationSessionRepository extends JpaRepository<ConversationSession, Long> {

    @EntityGraph(attributePaths = "messages")
    @Query("select s from ConversationSession s where s.id = :id")
    Optional<ConversationSession> findByIdWithMessages(Long id);

    @EntityGraph(attributePaths = "messages")
    @Query("select s from ConversationSession s order by s.updatedAt desc")
    List<ConversationSession> findAllWithMessages();
}
