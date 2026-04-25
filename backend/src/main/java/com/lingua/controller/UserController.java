package com.lingua.controller;

import com.lingua.model.User;
import com.lingua.model.UserProgress;
import com.lingua.repository.UserRepository;
import com.lingua.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://127.0.0.1:5173",
        "http://127.0.0.1:5174" })
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgressService progressService;

    @Autowired
    private com.lingua.service.EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username already exists"));
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        User savedUser = userRepository.save(user);

        // Send email in a background thread so as not to block the response
        if (savedUser.getEmail() != null && !savedUser.getEmail().isEmpty()) {
            new Thread(() -> {
                emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getName());
            }).start();
        }

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginReq) {
        Optional<User> existing = userRepository.findByUsername(loginReq.getUsername());
        if (existing.isPresent()) {
            User user = existing.get();
            if (user.getPassword() != null && user.getPassword().equals(loginReq.getPassword())) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid password"));
            }
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "User not found"));
        }
    }

    @GetMapping("/{userId}/progress/{langCode}")
    public UserProgress getProgress(@PathVariable Long userId, @PathVariable String langCode) {
        return progressService.getProgress(userId, langCode);
    }

    @PutMapping("/{userId}/languages")
    public ResponseEntity<?> updateLanguages(@PathVariable Long userId, @RequestBody Map<String, String> languages) {
        Optional<User> existing = userRepository.findById(userId);
        if (existing.isPresent()) {
            User user = existing.get();
            if (languages.containsKey("nativeLanguage")) {
                user.setNativeLanguage(languages.get("nativeLanguage"));
            }
            if (languages.containsKey("targetLanguage")) {
                user.setTargetLanguage(languages.get("targetLanguage"));
            }
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.status(404).body(Map.of("message", "User not found"));
    }
}
