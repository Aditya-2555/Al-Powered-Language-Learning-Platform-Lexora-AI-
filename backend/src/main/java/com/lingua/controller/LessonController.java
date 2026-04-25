package com.lingua.controller;

import com.lingua.model.Language;
import com.lingua.model.Lesson;
import com.lingua.model.UserProgress;
import com.lingua.service.LessonService;
import com.lingua.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://127.0.0.1:5173",
        "http://127.0.0.1:5174"
})
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private ProgressService progressService;

    @Autowired
    private com.lingua.repository.LanguageRepository languageRepository;

    @Autowired
    private com.lingua.repository.UserRepository userRepository;

    @GetMapping("/{languageCode}")
    public List<Lesson> getLessons(@PathVariable String languageCode,
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "Beginner") String difficulty) {

        var langOpt = languageRepository.findByCode(languageCode);
        var userOpt = userRepository.findById(userId);

        if (langOpt.isEmpty() || userOpt.isEmpty()) {
            return List.of();
        }

        Language lang = langOpt.get();

        Lesson generatedLesson = lessonService.getAdaptiveLesson(userId, lang, difficulty);

        if (generatedLesson == null) {
            return List.of();
        }

        return List.of(generatedLesson);
    }

    @PostMapping("/{userId}/complete/{languageCode}")
    public UserProgress completeLesson(@PathVariable Long userId,
            @PathVariable String languageCode,
            @RequestParam int xpEarned) {
        return progressService.addXp(userId, languageCode, xpEarned);
    }
}