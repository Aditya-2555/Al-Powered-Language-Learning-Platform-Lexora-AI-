package com.lingua.service;

import com.lingua.dto.DailyChallengeDTO;
import com.lingua.dto.ActivityResponseDTO;
import com.lingua.model.DailyChallenge;
import com.lingua.model.Language;
import com.lingua.model.User;
import com.lingua.repository.DailyChallengeRepository;
import com.lingua.repository.LanguageRepository;
import com.lingua.repository.UserRepository;
import com.lingua.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChallengeService {

    @Autowired
    private DailyChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private ProgressService progressService;

    public DailyChallengeDTO getDailyChallenge(Long userId, String languageCode) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);

        if (!userOpt.isPresent() || !langOpt.isPresent()) {
            return null;
        }

        User user = userOpt.get();
        Language lang = langOpt.get();
        LocalDate today = LocalDate.now();

        // Check if we already created a challenge for the user today
        Optional<DailyChallenge> existing = challengeRepository.findByUserIdAndLanguageIdAndChallengeDate(userId, lang.getId(), today);
        if (existing.isPresent()) {
            return mapToDTO(existing.get());
        }

        // Try to pull a random lesson to use as the framework for the daily challenge
        List<com.lingua.model.Lesson> allLessons = lessonRepository.findByLanguageId(lang.getId());
        
        if (allLessons.isEmpty()) {
            return null; // Don't crash, just gracefully show nothing if database is empty
        }

        int randomIndex = (int) (Math.random() * allLessons.size());
        com.lingua.model.Lesson randomLesson = allLessons.get(randomIndex);
        
        // Pass it through the template filler
        com.lingua.model.Lesson resolved = lessonService.instantiateTemplate(randomLesson);
        
        String question = resolved.getInstruction() + " " + resolved.getContent();
        String jsonOptions = resolved.getOptions();
        
        if (jsonOptions == null || jsonOptions.equals("[]")) {
            // Generate basic placeholder options if the database lesson doesn't have any
            jsonOptions = "[\"" + resolved.getCorrectAnswer() + "\", \"Incorrect Option A\", \"Incorrect Option B\"]";
        }

        DailyChallenge newChallenge = new DailyChallenge(
                user, 
                lang, 
                today,
                question,
                jsonOptions,
                resolved.getCorrectAnswer(),
                resolved.getExplanation()
        );
        
        DailyChallenge saved = challengeRepository.save(newChallenge);
        return mapToDTO(saved);
    }

    public ActivityResponseDTO submitChallengeAnswer(Long userId, String languageCode, String answer) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);

        if (!userOpt.isPresent() || !langOpt.isPresent()) {
            return new ActivityResponseDTO(0, 0, new ArrayList<>());
        }

        LocalDate today = LocalDate.now();
        Optional<DailyChallenge> existing = challengeRepository.findByUserIdAndLanguageIdAndChallengeDate(userId, langOpt.get().getId(), today);

        if (existing.isPresent()) {
            DailyChallenge challenge = existing.get();
            
            if (challenge.isCompleted()) {
                return new ActivityResponseDTO(0, 0, new ArrayList<>()); 
            }

            if (challenge.getCorrectAnswer().equalsIgnoreCase(answer)) {
                challenge.setCompleted(true);
                challengeRepository.save(challenge);

                progressService.addXp(userId, languageCode, 20);
                return new ActivityResponseDTO(20, 0, new ArrayList<>());
            }
        }
        
        return new ActivityResponseDTO(0, 0, new ArrayList<>());
    }

    private DailyChallengeDTO mapToDTO(DailyChallenge c) {
        return new DailyChallengeDTO(
                c.getId(),
                c.getQuestion(),
                c.getOptionsJson(),
                c.getCorrectAnswer(),
                c.getExplanation(),
                c.isCompleted()
        );
    }
}
