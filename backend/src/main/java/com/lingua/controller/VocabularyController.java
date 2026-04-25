package com.lingua.controller;

import com.lingua.dto.VocabularyDTO;
import com.lingua.service.VocabularyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notebook")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://127.0.0.1:5173", "http://127.0.0.1:5174" })
public class VocabularyController {

    @Autowired
    private VocabularyService vocabularyService;

    @PostMapping("/{userId}/{languageCode}")
    public ResponseEntity<VocabularyDTO> addVocabulary(@PathVariable Long userId, @PathVariable String languageCode, @RequestBody VocabularyDTO dto) {
        VocabularyDTO saved = vocabularyService.addVocabulary(userId, languageCode, dto);
        if (saved != null) return ResponseEntity.ok(saved);
        return ResponseEntity.badRequest().build(); // Assuming failure due to duplicate
    }

    @GetMapping("/{userId}/{languageCode}")
    public ResponseEntity<List<VocabularyDTO>> getNotebook(@PathVariable Long userId, @PathVariable String languageCode) {
        return ResponseEntity.ok(vocabularyService.getNotebook(userId, languageCode));
    }

    @PatchMapping("/{entryId}/favorite")
    public ResponseEntity<VocabularyDTO> toggleFavorite(@PathVariable Long entryId) {
        VocabularyDTO updated = vocabularyService.toggleFavorite(entryId);
        if (updated != null) return ResponseEntity.ok(updated);
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/review/{entryId}")
    public ResponseEntity<VocabularyDTO> reviewEntry(@PathVariable Long entryId, @RequestParam boolean isCorrect) {
        VocabularyDTO updated = vocabularyService.reviewEntry(entryId, isCorrect);
        if (updated != null) return ResponseEntity.ok(updated);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long entryId) {
        if (vocabularyService.deleteEntry(entryId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
