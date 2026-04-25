package com.lingua.controller;

import com.lingua.dto.SpeakingAttemptDTO;
import com.lingua.model.Language;
import com.lingua.model.SpeakingAttempt;
import com.lingua.model.User;
import com.lingua.repository.LanguageRepository;
import com.lingua.repository.SpeakingAttemptRepository;
import com.lingua.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/speaking")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://127.0.0.1:5173", "http://127.0.0.1:5174" })
public class SpeakingController {

    @Autowired
    private SpeakingAttemptRepository speakingAttemptRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @PostMapping("/{userId}/{languageCode}")
    public ResponseEntity<Void> recordAttempt(@PathVariable Long userId, @PathVariable String languageCode, @RequestBody SpeakingAttemptDTO attemptDTO) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);

        if (userOpt.isPresent() && langOpt.isPresent()) {
            SpeakingAttempt attempt = new SpeakingAttempt(
                    userOpt.get(),
                    langOpt.get(),
                    attemptDTO.getTargetPhrase(),
                    attemptDTO.getTranscribedText(),
                    attemptDTO.getConfidenceScore()
            );
            speakingAttemptRepository.save(attempt);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{userId}/{languageCode}")
    public ResponseEntity<List<SpeakingAttemptDTO>> getHistory(@PathVariable Long userId, @PathVariable String languageCode) {
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);
        if (langOpt.isPresent()) {
            List<SpeakingAttemptDTO> history = speakingAttemptRepository.findByUserIdAndLanguageIdOrderByAttemptedAtDesc(userId, langOpt.get().getId())
                    .stream()
                    .map(a -> new SpeakingAttemptDTO(a.getTargetPhrase(), a.getTranscribedText(), a.getConfidenceScore(), a.getAttemptedAt()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(history);
        }
        return ResponseEntity.notFound().build();
    }
}
