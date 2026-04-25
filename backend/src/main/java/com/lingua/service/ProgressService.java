package com.lingua.service;

import com.lingua.model.Language;
import com.lingua.model.User;
import com.lingua.model.UserProgress;
import com.lingua.repository.LanguageRepository;
import com.lingua.repository.UserProgressRepository;
import com.lingua.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.lingua.model.LearningActivity;
import org.springframework.context.annotation.Lazy;

@Service
public class ProgressService {

    @Autowired
    private UserProgressRepository progressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    @Lazy
    private GoalService goalService;

    public UserProgress addXp(Long userId, String languageCode, int xpAmount) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);

        if (userOpt.isPresent() && langOpt.isPresent()) {
            User user = userOpt.get();
            Language lang = langOpt.get();

            Optional<UserProgress> progressOpt = progressRepository.findByUserIdAndLanguageId(user.getId(),
                    lang.getId());
            UserProgress progress;

            if (progressOpt.isPresent()) {
                progress = progressOpt.get();
                progress.setXp(progress.getXp() + xpAmount);
            } else {
                progress = new UserProgress(user, lang, xpAmount, 1);
            }

            // Level up logic (every 100 xp is a level)
            progress.setLevel((progress.getXp() / 100) + 1);

            goalService.incrementGoal(userId, languageCode, "XP", xpAmount);

            return progressRepository.save(progress);
        }
        return null; // Return error or handle gracefully
    }

    public UserProgress getProgress(Long userId, String languageCode) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);

        if (userOpt.isPresent() && langOpt.isPresent()) {
            return progressRepository.findByUserIdAndLanguageId(userId, langOpt.get().getId())
                    .orElse(new UserProgress(userOpt.get(), langOpt.get(), 0, 1));
        }
        return null;
    }

    @Autowired
    private com.lingua.repository.LearningActivityRepository activityRepository;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private com.lingua.repository.TopicPerformanceRepository topicPerformanceRepository;

    public com.lingua.dto.ActivityResponseDTO logActivity(Long userId, String languageCode, String activityType, String topic, Long sourceLessonId, Boolean isCorrect, String content, String mistake, String correctAnswer, String explanation) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);

        if (userOpt.isPresent() && langOpt.isPresent()) {
            User user = userOpt.get();
            Language lang = langOpt.get();
            int xpEarned = 0;

            com.lingua.model.LearningActivity activity = new com.lingua.model.LearningActivity(
                user, lang, activityType, topic, sourceLessonId, isCorrect, content, mistake, correctAnswer, explanation
            );
            activityRepository.save(activity);

            if (topic != null && isCorrect != null) {
                 com.lingua.model.TopicPerformance perf = topicPerformanceRepository.findByUserIdAndLanguageIdAndTopic(userId, lang.getId(), topic)
                     .orElseGet(() -> new com.lingua.model.TopicPerformance(user, lang, topic));
                 perf.recordAttempt(isCorrect);
                 topicPerformanceRepository.save(perf);
            }
            UserProgress progress = getProgress(userId, languageCode);
            if (progress.getId() == null) {
                progress = progressRepository.save(progress);
            }

            java.time.LocalDate today = java.time.LocalDate.now();
            if (progress.getLastActivityDate() == null) {
                progress.setCurrentStreak(1);
            } else if (progress.getLastActivityDate().isEqual(today.minusDays(1))) {
                progress.setCurrentStreak(progress.getCurrentStreak() + 1);
            } else if (!progress.getLastActivityDate().isEqual(today)) {
                progress.setCurrentStreak(1); // Reset streak
            }
            progress.setLastActivityDate(today);

            if (isCorrect != null) {
                progress.setTotalExercisesAttempted(progress.getTotalExercisesAttempted() + 1);
                if (isCorrect) {
                    progress.setCorrectExercises(progress.getCorrectExercises() + 1);
                    xpEarned = 10;
                    progress.setXp(progress.getXp() + xpEarned);
                    goalService.incrementGoal(userId, languageCode, "XP", xpEarned);
                }
            }

            progress.setLevel((progress.getXp() / 100) + 1);
            progressRepository.save(progress);

            // Evaluate achievements if they got it right
            List<com.lingua.dto.AchievementDTO> newUnlocks = achievementService.checkAndUnlockAchievements(user, progress);
            if (isCorrect != null && isCorrect && topic != null) {
                achievementService.evaluateTopicAchievement(user, topic, newUnlocks);
            }

            return new com.lingua.dto.ActivityResponseDTO(xpEarned, 0, newUnlocks);
        }
        return new com.lingua.dto.ActivityResponseDTO(0, 0, new java.util.ArrayList<>());
    }

    public com.lingua.dto.DashboardStatsDTO getDashboardStats(Long userId, String languageCode) {
        UserProgress progress = getProgress(userId, languageCode);
        if (progress == null) return null;

        com.lingua.dto.DashboardStatsDTO stats = new com.lingua.dto.DashboardStatsDTO();
        stats.setTotalXp(progress.getXp());
        stats.setCurrentStreak(progress.getCurrentStreak());
        stats.setLessonsCompleted(progress.getLessonsCompleted());
        stats.setLevelProgressPercentage(progress.getXp() % 100);
        stats.setCurrentLevel(progress.getLevel());
        stats.setTargetLanguage(progress.getLanguage().getName());

        if (progress.getTotalExercisesAttempted() > 0) {
            stats.setAccuracyPercentage(Math.round(((double) progress.getCorrectExercises() / progress.getTotalExercisesAttempted()) * 100.0 * 10.0) / 10.0);
        } else {
            stats.setAccuracyPercentage(0.0);
        }

        // Fetch recent activities
        List<com.lingua.model.LearningActivity> activities = activityRepository.findByUserIdAndLanguageIdOrderByCreatedAtDesc(userId, progress.getLanguage().getId());
        stats.setRecentActivities(activities.size() > 5 ? activities.subList(0, 5) : activities);
        
        // Calculate Weekly Activity Pulse
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate startOfWeek = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        List<Integer> pulse = new ArrayList<>(java.util.Collections.nCopies(7, 0));
        for (com.lingua.model.LearningActivity a : activities) {
            java.time.LocalDate actDate = a.getCreatedAt().toLocalDate();
            if (!actDate.isBefore(startOfWeek) && actDate.isBefore(startOfWeek.plusDays(7))) {
                int dayIndex = actDate.getDayOfWeek().getValue() - 1; // 0 for Mon
                int xpEstimate = (a.getIsCorrect() != null && a.getIsCorrect()) ? 10 : 5;
                pulse.set(dayIndex, pulse.get(dayIndex) + xpEstimate);
            }
        }
        stats.setWeeklyActivityPulse(pulse);
        
        List<com.lingua.model.LearningActivity> mistakes = activityRepository.findByUserIdAndLanguageIdAndIsCorrectFalseOrderByCreatedAtDesc(userId, progress.getLanguage().getId());
        stats.setRecentMistakes(mistakes.size() > 5 ? mistakes.subList(0, 5) : mistakes);

        // Determine strongest/weakest topics dynamically using map counts
        java.util.Map<String, int[]> topicStats = new java.util.HashMap<>();
        for (com.lingua.model.LearningActivity a : activities) {
            if (a.getTopic() != null && a.getIsCorrect() != null) {
                topicStats.putIfAbsent(a.getTopic(), new int[]{0, 0});
                topicStats.get(a.getTopic())[0]++; // total
                if (a.getIsCorrect()) topicStats.get(a.getTopic())[1]++; // correct
            }
        }

        String strongest = "N/A";
        String weakest = "N/A";
        double highestAcc = -1;
        double lowestAcc = 101;

        for (java.util.Map.Entry<String, int[]> entry : topicStats.entrySet()) {
            if (entry.getValue()[0] < 2) continue; // Need at least 2 attempts to judge
            double acc = (double) entry.getValue()[1] / entry.getValue()[0];
            if (acc > highestAcc) { highestAcc = acc; strongest = entry.getKey(); }
            if (acc < lowestAcc) { lowestAcc = acc; weakest = entry.getKey(); }
        }

        stats.setStrongestTopic(strongest);
        stats.setWeakestTopic(weakest);

        // Dynamic Recommended Action
        if (mistakes.size() > 3 && lowestAcc < 0.6) {
            stats.setRecommendedAction(new com.lingua.dto.DashboardStatsDTO.RecommendedAction("REVISE_MISTAKES", "Review your recent mistakes in " + weakest));
        } else {
            stats.setRecommendedAction(new com.lingua.dto.DashboardStatsDTO.RecommendedAction("NEXT_LESSON", "Start a new lesson"));
        }

        return stats;
    }

    public List<LearningActivity> getUnresolvedMistakes(Long userId, String languageCode) {
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);
        if (langOpt.isPresent()) {
            return activityRepository.findByUserIdAndLanguageIdOrderByCreatedAtDesc(userId, langOpt.get().getId())
                    .stream()
                    .filter(a -> a.getIsCorrect() != null && !a.getIsCorrect() && !a.getResolved())
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<LearningActivity> getLessonHistory(Long userId, String languageCode) {
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);
        if (langOpt.isPresent()) {
            return activityRepository.findByUserIdAndLanguageIdOrderByCreatedAtDesc(userId, langOpt.get().getId())
                    .stream()
                    .filter(a -> "LESSON".equals(a.getActivityType()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public com.lingua.dto.ActivityResponseDTO resolveMistake(Long activityId) {
        Optional<com.lingua.model.LearningActivity> actOpt = activityRepository.findById(activityId);
        if (actOpt.isPresent()) {
            com.lingua.model.LearningActivity act = actOpt.get();
            act.setResolved(true);
            activityRepository.save(act);

            // Award XP for resolving mistake
            UserProgress progress = progressRepository.findByUserIdAndLanguageId(act.getUser().getId(), act.getLanguage().getId()).orElse(null);
            List<com.lingua.dto.AchievementDTO> unlocks = new java.util.ArrayList<>();
            int bonusXp = 5;

            if (progress != null) {
                progress.setXp(progress.getXp() + bonusXp);
                progress.setLevel((progress.getXp() / 100) + 1);
                
                goalService.incrementGoal(act.getUser().getId(), act.getLanguage().getCode(), "XP", bonusXp);

                progressRepository.save(progress);
                unlocks = achievementService.checkAndUnlockAchievements(act.getUser(), progress);
            }

            return new com.lingua.dto.ActivityResponseDTO(bonusXp, 0, unlocks);
        }
        return new com.lingua.dto.ActivityResponseDTO(0, 0, new java.util.ArrayList<>());
    }
}
