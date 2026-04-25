package com.lingua.service;

import com.lingua.dto.VocabularyDTO;
import com.lingua.model.Language;
import com.lingua.model.User;
import com.lingua.model.VocabularyEntry;
import com.lingua.repository.LanguageRepository;
import com.lingua.repository.UserRepository;
import com.lingua.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;

@Service
public class VocabularyService {

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    @Lazy
    private GoalService goalService;

    public VocabularyDTO addVocabulary(Long userId, String languageCode, VocabularyDTO dto) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);

        if (userOpt.isPresent() && langOpt.isPresent()) {
            User user = userOpt.get();
            Language lang = langOpt.get();

            // Prevent strict duplicate word submissions across the same language profile
            if (vocabularyRepository.existsByUserIdAndLanguageIdAndTargetWordIgnoreCase(user.getId(), lang.getId(), dto.getTargetWord())) {
                return null;
            }

            VocabularyEntry entry = new VocabularyEntry(
                user, lang, dto.getTargetWord(), dto.getNativeMeaning(), dto.getPartOfSpeech(),
                dto.getExampleTarget(), dto.getExampleNative(), dto.getDifficulty(), dto.getSource()
            );

            vocabularyRepository.save(entry);
            goalService.incrementGoal(userId, languageCode, "VOCAB", 1);
            return mapToDto(entry);
        }
        return null;
    }

    public List<VocabularyDTO> getNotebook(Long userId, String languageCode) {
        Optional<Language> langOpt = languageRepository.findByCode(languageCode);
        if (langOpt.isPresent()) {
            return vocabularyRepository.findByUserIdAndLanguageIdOrderByCreatedAtDesc(userId, langOpt.get().getId())
                .stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return new java.util.ArrayList<>();
    }

    public VocabularyDTO toggleFavorite(Long entryId) {
        Optional<VocabularyEntry> entryOpt = vocabularyRepository.findById(entryId);
        if (entryOpt.isPresent()) {
            VocabularyEntry entry = entryOpt.get();
            entry.setIsFavorite(!entry.getIsFavorite());
            vocabularyRepository.save(entry);
            return mapToDto(entry);
        }
        return null;
    }

    public VocabularyDTO reviewEntry(Long entryId, boolean isCorrect) {
        Optional<VocabularyEntry> entryOpt = vocabularyRepository.findById(entryId);
        if (entryOpt.isPresent()) {
            VocabularyEntry entry = entryOpt.get();
            
            if (isCorrect) {
                // If correct, push revision date based on count
                entry.setRevisionCount(entry.getRevisionCount() + 1);
                
                int daysToAdd;
                if (entry.getRevisionCount() == 1) daysToAdd = 1;
                else if (entry.getRevisionCount() == 2) daysToAdd = 3;
                else daysToAdd = 7;
                
                entry.setNextRevisionDue(LocalDateTime.now().plusDays(daysToAdd));
            } else {
                // If wrong, reset intervals and review count, make due tomorrow
                entry.setRevisionCount(0);
                entry.setNextRevisionDue(LocalDateTime.now().plusDays(1));
            }
            
            entry.setLastRevisedAt(LocalDateTime.now());
            
            vocabularyRepository.save(entry);
            return mapToDto(entry);
        }
        return null;
    }

    public boolean deleteEntry(Long entryId) {
        if (vocabularyRepository.existsById(entryId)) {
            vocabularyRepository.deleteById(entryId);
            return true;
        }
        return false;
    }

    private VocabularyDTO mapToDto(VocabularyEntry e) {
        return new VocabularyDTO(
            e.getId(), e.getTargetWord(), e.getNativeMeaning(), e.getPartOfSpeech(),
            e.getExampleTarget(), e.getExampleNative(), e.getDifficulty(), e.getSource(),
            e.getIsFavorite(), e.getNextRevisionDue(), e.getRevisionCount()
        );
    }
}
