package com.lingua.controller;

import com.lingua.dto.ChatRequest;
import com.lingua.dto.ChatResponse;
import com.lingua.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://127.0.0.1:5173",
        "http://127.0.0.1:5174" })
public class ChatController {

    @Autowired
    private TutorService tutorService;

    @PostMapping("/send")
    public com.lingua.dto.AdvancedChatResponse sendMessage(@RequestBody ChatRequest request, @RequestParam(required = false) Long sessionId) {
        return tutorService.processChat(request, sessionId);
    }

    @PostMapping("/session/start/{userId}/{languageCode}")
    public com.lingua.dto.ChatSessionDTO startSession(@PathVariable Long userId, @PathVariable String languageCode) {
        return tutorService.startSession(userId, languageCode);
    }

    @GetMapping("/sessions/{userId}/{languageCode}")
    public java.util.List<com.lingua.dto.ChatSessionDTO> getSessions(@PathVariable Long userId, @PathVariable String languageCode) {
        return tutorService.getSessions(userId, languageCode);
    }

    @GetMapping("/messages/{sessionId}")
    public java.util.List<com.lingua.dto.ChatMessageDTO> getMessages(@PathVariable Long sessionId) {
        return tutorService.getMessages(sessionId);
    }

    @PostMapping("/scenario/start/{userId}/{languageCode}")
    public com.lingua.dto.ChatSessionDTO startScenarioSession(@PathVariable Long userId, @PathVariable String languageCode, @RequestParam String scenario) {
        return tutorService.startScenarioSession(userId, languageCode, scenario);
    }

    @PostMapping("/scenario/evaluate/{sessionId}")
    public String evaluateScenario(@PathVariable Long sessionId) {
        return tutorService.evaluateScenario(sessionId);
    }
}
