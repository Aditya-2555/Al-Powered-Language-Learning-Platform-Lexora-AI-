package com.lingua.controller;

import com.lingua.model.User;
import com.lingua.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://127.0.0.1:5173",
        "http://127.0.0.1:5174" })
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/{adminId}")
    public List<User> getAllUsers(@PathVariable Long adminId) {
        User admin = userRepository.findById(adminId).orElseThrow(() -> new RuntimeException("Admin not found"));
        if (!"ADMIN".equals(admin.getRole())) {
            throw new RuntimeException("Unauthorized: Requires Admin privileges");
        }
        return userRepository.findAll();
    }
}
