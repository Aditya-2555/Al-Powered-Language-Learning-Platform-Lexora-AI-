package com.lingua.service;

import com.lingua.dto.DailyGoalDTO;
import com.lingua.model.DailyGoal;
import com.lingua.model.Language;
import com.lingua.model.User;
import com.lingua.repository.DailyGoalRepository;
import com.lingua.repository.LanguageRepository;
import com.lingua.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GoalService {

    @Autowired
    private DailyGoalRepository dailyGoalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private ProgressService progressService;

    public List<DailyGoalDTO> getDailyGoals(Long userId, String languageCode) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);

        if (userOpt.isPresent() && langOpt.isPresent()) {
            User user = userOpt.get();
            Language lang = langOpt.get();
            LocalDate today = LocalDate.now();

            List<DailyGoal> todayGoals = dailyGoalRepository.findByUserIdAndLanguageIdAndGoalDate(user.getId(), lang.getId(), today);

            if (todayGoals.isEmpty()) {
                // Initialize goals for the day
                todayGoals = List.of(
                        new DailyGoal(user, lang, "LESSON", 1, today),
                        new DailyGoal(user, lang, "XP", 10, today),
                        new DailyGoal(user, lang, "VOCAB", 5, today)
                );
                dailyGoalRepository.saveAll(todayGoals);
            }

            return todayGoals.stream()
                    .map(g -> new DailyGoalDTO(g.getGoalType(), g.getTargetValue(), g.getCurrentValue(), g.isCompleted()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public void incrementGoal(Long userId, String languageCode, String goalType, int amount) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);

        if (userOpt.isPresent() && langOpt.isPresent()) {
            LocalDate today = LocalDate.now();
            List<DailyGoal> todayGoals = dailyGoalRepository.findByUserIdAndLanguageIdAndGoalDate(userId, langOpt.get().getId(), today);

            for (DailyGoal goal : todayGoals) {
                if (goal.getGoalType().equals(goalType) && !goal.isCompleted()) {
                    goal.setCurrentValue(goal.getCurrentValue() + amount);
                    boolean newlyCompleted = goal.isCompleted();
                    dailyGoalRepository.save(goal);
                    
                    if (newlyCompleted && !goalType.equals("XP")) {
                        progressService.addXp(userId, languageCode, 10);
                    }
                }
            }
        }
    }
}
