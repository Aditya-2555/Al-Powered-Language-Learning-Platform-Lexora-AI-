package com.lingua.controller;

import com.lingua.dto.DailyGoalDTO;
import com.lingua.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://127.0.0.1:5173", "http://127.0.0.1:5174" })
public class GoalController {

    @Autowired
    private GoalService goalService;

    @GetMapping
    public ResponseEntity<List<DailyGoalDTO>> getDailyGoals(
            @RequestParam Long userId,
            @RequestParam String languageCode) {
        
        List<DailyGoalDTO> goals = goalService.getDailyGoals(userId, languageCode);
        return ResponseEntity.ok(goals);
    }
}
