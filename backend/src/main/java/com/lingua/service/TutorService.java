package com.lingua.service;

import com.lingua.dto.ChatRequest;
import com.lingua.dto.ChatSessionDTO;
import com.lingua.dto.ChatMessageDTO;
import com.lingua.model.ChatMessage;
import com.lingua.model.ChatSession;
import com.lingua.model.Language;
import com.lingua.model.User;
import com.lingua.repository.ChatMessageRepository;
import com.lingua.repository.ChatSessionRepository;
import com.lingua.repository.LanguageRepository;
import com.lingua.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TutorService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private GeminiService geminiService;

    public ChatSessionDTO startSession(Long userId, String languageCode) {
        return startScenarioSession(userId, languageCode, null);
    }

    public ChatSessionDTO startScenarioSession(Long userId, String languageCode, String scenario) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);
        if (userOpt.isPresent() && langOpt.isPresent()) {
            String title = (scenario != null && !scenario.isEmpty()) ? "Scenario - " + scenario : "Conversation - " + java.time.LocalDate.now().toString();
            ChatSession session = new ChatSession(userOpt.get(), langOpt.get(), title);
            chatSessionRepository.save(session);
            return new ChatSessionDTO(session.getId(), session.getTitle(), session.getStartedAt());
        }
        return null;
    }

    public String evaluateScenario(Long sessionId) {
         Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
         if (sessionOpt.isEmpty()) return "{\"fluencyScore\": 0, \"grammarIssues\": [], \"suggestedVocabulary\": [], \"overallFeedback\": \"Session not found\"}";
         
         ChatSession session = sessionOpt.get();
         List<ChatMessage> history = chatMessageRepository.findBySessionIdOrderBySentAtAsc(session.getId());
         
         String targetLang = getLanguageName(session.getLanguage().getCode());
         String nativeLang = getLanguageName(session.getUser().getNativeLanguage());
         String level = "Intermediate"; // Hardcoded default for scenarios since level is tracked in UserProgress separately
         
         return geminiService.evaluateScenario(targetLang, nativeLang, level, history);
    }

    public List<ChatSessionDTO> getSessions(Long userId, String languageCode) {
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);
        if (langOpt.isPresent()) {
            return chatSessionRepository.findByUserIdAndLanguageIdOrderByStartedAtDesc(userId, langOpt.get().getId())
                .stream().map(s -> new ChatSessionDTO(s.getId(), s.getTitle(), s.getStartedAt()))
                .collect(Collectors.toList());
        }
        return new java.util.ArrayList<>();
    }

    public List<ChatMessageDTO> getMessages(Long sessionId) {
        return chatMessageRepository.findBySessionIdOrderBySentAtAsc(sessionId)
                .stream().map(m -> new ChatMessageDTO(m.getRole(), m.getContent(), m.getTranslation(), m.getExplanation(), m.getCorrection()))
                .collect(Collectors.toList());
    }

    public com.lingua.dto.AdvancedChatResponse processChat(ChatRequest request, Long sessionId) {
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return new com.lingua.dto.AdvancedChatResponse("User not found.", "", "", "", "");
        }
        User user = userOpt.get();

        ChatSession session = null;
        if (sessionId != null) {
            session = chatSessionRepository.findById(sessionId).orElse(null);
        }
        if (session == null) {
            // Find existing default session for user and language instead of always creating
            Language lang = languageRepository.findByCode(request.getLanguageCode()).orElse(null);
            if (lang != null) {
                List<ChatSession> existingSessions = chatSessionRepository.findByUserIdAndLanguageIdOrderByStartedAtDesc(user.getId(), lang.getId());
                if (!existingSessions.isEmpty()) {
                    session = existingSessions.get(0);
                }
            }
            if (session == null) {
                session = new ChatSession(user, lang, "Auto Conversation");
                chatSessionRepository.save(session);
            }
        }

        // Save user message
        ChatMessage userMsg = new ChatMessage(session, "user", request.getMessage(), null, null, null);
        chatMessageRepository.save(userMsg);

        // Fetch recent chat history for AI context
        List<ChatMessage> history = chatMessageRepository.findBySessionIdOrderBySentAtAsc(session.getId());

        // Map the language code to the full name
        String langName = getLanguageName(request.getLanguageCode());
        String nativeLangName = getLanguageName(user.getNativeLanguage());

        String level = request.getLevel();
        if (level == null || level.isEmpty()) {
            level = "Beginner";
        }

        // Generate dynamic AI response (passing the history to GeminiService - may require GeminiService to iterate over ChatMessage objects rather than old schema)
        com.lingua.dto.AdvancedChatResponse aiReply = geminiService.generateChatResponse(langName, nativeLangName, level, request.getMessage(), history, request.getScenario());

        // Save AI structured message
        ChatMessage aiMsg = new ChatMessage(session, "tutor", aiReply.getReplyInTargetLanguage(), aiReply.getTranslationInNativeLanguage(), aiReply.getGrammarTipInNativeLanguage(), aiReply.getCorrectionInNativeLanguage());
        chatMessageRepository.save(aiMsg);

        return aiReply;
    }

    private String getLanguageName(String code) {
        if (code == null) return "English";
        switch (code.toLowerCase()) {
            case "es": return "Spanish";
            case "fr": return "French";
            case "de": return "German";
            case "en": default: return "English";
        }
    }
}
