package com.lingua.service;

import com.lingua.dto.RecommendationDTO;
import com.lingua.model.Language;
import com.lingua.model.TopicPerformance;
import com.lingua.model.VocabularyEntry;
import com.lingua.repository.LanguageRepository;
import com.lingua.repository.TopicPerformanceRepository;
import com.lingua.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private TopicPerformanceRepository topicPerformanceRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private ProgressService progressService;

    @Autowired
    private LanguageRepository languageRepository;

    public RecommendationDTO getNextBestAction(Long userId, String languageCode) {
        Language lang = languageRepository.findByCode(languageCode).orElse(null);
        if (lang == null) return genericRecommendation();

        // Check 1: Spaced Repetition Debt
        List<VocabularyEntry> vocabList = vocabularyRepository.findByUserIdAndLanguageIdOrderByCreatedAtDesc(userId, lang.getId());
        long dueVocabCount = vocabList.stream()
                .filter(v -> v.getNextRevisionDue() != null && LocalDateTime.now().isAfter(v.getNextRevisionDue()))
                .count();

        if (dueVocabCount >= 5) {
            return new RecommendationDTO(
                "VOCABULARY",
                "Spaced Repetition Review",
                String.format("You have %d vocabulary words explicitly due for memory reinforcement today.", dueVocabCount),
                null, "Vocabulary",
                0.95
            );
        }

        // Check 2: Mistake Vault Depth
        int unresolvedMistakes = progressService.getUnresolvedMistakes(userId, languageCode).size();
        if (unresolvedMistakes >= 10) {
            return new RecommendationDTO(
                "MISTAKES",
                "Resolve Learning Debts",
                String.format("Your vault has reached %d unresolved errors. Clear them out before moving forward.", unresolvedMistakes),
                null, "Mixed Review",
                0.88
            );
        }

        // Check 3: Adaptive Intelligence (Find weakest topic)
        List<TopicPerformance> performances = topicPerformanceRepository.findByUserIdAndLanguageIdOrderByRecentAccuracyAsc(userId, lang.getId());
        if (!performances.isEmpty()) {
            TopicPerformance weakest = performances.get(0);
            
            // Adjust difficulty dynamically based on the accuracy
            String recommendedDifficulty = "Beginner";
            if (weakest.getRecentAccuracy() > 0.75) recommendedDifficulty = "Advanced";
            else if (weakest.getRecentAccuracy() > 0.40) recommendedDifficulty = "Intermediate";

            return new RecommendationDTO(
                "LESSON",
                "Adaptive Focus Lesson: " + weakest.getTopic(),
                String.format("Our algorithm detected your recent accuracy in '%s' dropped to %.0f%%. Let's patch that weakness immediately.", 
                              weakest.getTopic(), weakest.getRecentAccuracy() * 100),
                recommendedDifficulty, 
                weakest.getTopic(),
                0.92
            );
        }

        // Default if brand new user with tracking data yet
        return genericRecommendation();
    }

    private RecommendationDTO genericRecommendation() {
        return new RecommendationDTO(
            "LESSON",
            "General Practice",
            "Embark on a standard mixed-format lesson to continue building your foundation.",
            "Beginner", "Mixed", 0.50
        );
    }
}
