package com.lingua.controller;

import com.lingua.dto.DailyChallengeDTO;
import com.lingua.dto.ActivityResponseDTO;
import com.lingua.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/challenge")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://127.0.0.1:5173", "http://127.0.0.1:5174" })
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @GetMapping
    public ResponseEntity<DailyChallengeDTO> getDailyChallenge(
            @RequestParam Long userId,
            @RequestParam String languageCode) {
        DailyChallengeDTO challenge = challengeService.getDailyChallenge(userId, languageCode);
        if (challenge != null) {
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/submit")
    public ResponseEntity<ActivityResponseDTO> submitChallenge(
            @RequestParam Long userId,
            @RequestParam String languageCode,
            @RequestBody Map<String, String> payload) {
        String answer = payload.get("answer");
        ActivityResponseDTO result = challengeService.submitChallengeAnswer(userId, languageCode, answer);
        return ResponseEntity.ok(result);
    }
}
