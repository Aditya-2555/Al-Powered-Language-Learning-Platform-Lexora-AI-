package com.lingua.repository;

import com.lingua.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findByUserIdAndLanguageIdOrderByStartedAtDesc(Long userId, Long languageId);
}
