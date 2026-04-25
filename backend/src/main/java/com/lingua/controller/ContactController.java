package com.lingua.controller;

import com.lingua.model.ContactMessage;
import com.lingua.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://127.0.0.1:5173",
        "http://127.0.0.1:5174" })
public class ContactController {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @PostMapping
    public ResponseEntity<?> submitContactMessage(@RequestBody ContactMessage message) {
        if (message.getName() == null || message.getEmail() == null || message.getMessage() == null) {
            return ResponseEntity.badRequest().body("Name, Email, and Message are required");
        }
        ContactMessage saved = contactMessageRepository.save(message);
        return ResponseEntity.ok(saved);
    }
}
