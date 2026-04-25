package com.lingua.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingua.model.Language;
import com.lingua.model.Lesson;
import com.lingua.model.TopicPerformance;
import com.lingua.repository.LearningActivityRepository;
import com.lingua.repository.LessonRepository;
import com.lingua.repository.TopicPerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class LessonService {

    private final Random random = new Random();

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LearningActivityRepository activityRepository;

    @Autowired
    private TopicPerformanceRepository topicPerformanceRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    public Lesson getAdaptiveLesson(Long userId, Language lang, String difficulty) {
        // 1. Fetch recent seen lesson IDs to prevent repetition
        List<Long> recentSeenIds = activityRepository.findRecentSourceLessonIds(
                userId,
                lang.getId(),
                PageRequest.of(0, 50)
        );

        // 2. Determine weak topic (if any)
        List<TopicPerformance> performances =
                topicPerformanceRepository.findByUserIdAndLanguageId(userId, lang.getId());

        String weakTopic = null;
        double lowestAcc = 100.0;

        for (TopicPerformance tp : performances) {
            if (tp.getTotalAttempts() >= 2 && tp.getRecentAccuracy() < 0.6) {
                if (tp.getRecentAccuracy() < lowestAcc) {
                    lowestAcc = tp.getRecentAccuracy();
                    weakTopic = tp.getTopic();
                }
            }
        }

        // 3. Select potential lessons from DB
        List<Lesson> candidates = new ArrayList<>();

        if (weakTopic != null) {
            candidates = lessonRepository.findByLanguageIdAndDifficultyAndTopic(
                    lang.getId(),
                    difficulty,
                    weakTopic
            );

            candidates.removeIf(l ->
                    recentSeenIds.contains(l.getId()) || !isUsableTemplate(l)
            );
        }

        // Fallback to general pool
        if (candidates.isEmpty()) {
            List<Lesson> all = lessonRepository.findByLanguageIdAndDifficulty(lang.getId(), difficulty);

            List<Lesson> unseenCandidates = new ArrayList<>();
            for (Lesson l : all) {
                if (!recentSeenIds.contains(l.getId()) && isUsableTemplate(l)) {
                    unseenCandidates.add(l);
                }
            }

            if (!unseenCandidates.isEmpty()) {
                candidates = unseenCandidates;
            } else {
                // If all were seen already, at least keep valid ones
                for (Lesson l : all) {
                    if (isUsableTemplate(l)) {
                        candidates.add(l);
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            return buildFallbackLesson(lang, difficulty);
        }

        Lesson chosenTemplate = candidates.get(random.nextInt(candidates.size()));

        // 4. Apply Dynamic Vocabulary Template Generation
        return instantiateTemplate(chosenTemplate);
    }

    public Lesson instantiateTemplate(Lesson template) {
        String code = template.getLanguage().getCode();

        Lesson resolvedLesson = new Lesson();
        resolvedLesson.setId(template.getId());
        resolvedLesson.setLanguage(template.getLanguage());
        resolvedLesson.setType(firstNonBlank(template.getType(), "Translate"));
        resolvedLesson.setDifficulty(firstNonBlank(template.getDifficulty(), "Beginner"));
        resolvedLesson.setTopic(firstNonBlank(template.getTopic(), "General"));

        String type = resolvedLesson.getType();

        // Instruction resolve
        String instructionTemplate = firstNonBlank(template.getInstruction(), inferInstruction(type));
        String[] instrRes = VocabularyBank.applyTemplate(instructionTemplate, "", code);
        resolvedLesson.setInstruction(firstNonBlank(instrRes[0], instructionTemplate));

        /*
         * IMPORTANT FIX:
         * Teri kuch Translate rows me:
         * - content blank hai
         * - explanation English meaning hai
         * - correctAnswer target language phrase hai
         *
         * Example:
         * content = ""
         * explanation = "Hello, how are you?"
         * correctAnswer = "Hola, ¿cómo estás?"
         *
         * To Translate lesson ke liye:
         * content => target language phrase
         * correctAnswer => native language meaning
         */

        if ("Translate".equalsIgnoreCase(type)) {
            String rawContent;
            String rawCorrectAnswer;
            String rawExplanation;

            boolean brokenTranslateShape =
                    isBlank(template.getContent())
                            && !isBlank(template.getCorrectAnswer())
                            && !isBlank(template.getExplanation());

            if (brokenTranslateShape) {
                // swap/fix old broken mapping
                rawContent = template.getCorrectAnswer();
                rawCorrectAnswer = template.getExplanation();
                rawExplanation = "Translate the given phrase carefully.";
            } else {
                rawContent = firstNonBlank(template.getContent(), template.getCorrectAnswer());
                rawCorrectAnswer = firstNonBlank(template.getCorrectAnswer(), template.getExplanation());
                rawExplanation = firstNonBlank(
                        template.getExplanation(),
                        "Translate the given phrase carefully."
                );
            }

            String[] contentRes = VocabularyBank.applyTemplate("", rawContent, code);
            String[] answerRes = VocabularyBank.applyTemplate("", rawCorrectAnswer, code);
            String[] explanationRes = VocabularyBank.applyTemplate(rawExplanation, "", code);

            resolvedLesson.setContent(firstNonBlank(contentRes[1], rawContent));
            resolvedLesson.setCorrectAnswer(firstNonBlank(answerRes[1], rawCorrectAnswer));
            resolvedLesson.setExplanation(firstNonBlank(explanationRes[0], rawExplanation));
        } else {
            // Normal content/explanation mapping
            String[] resolved = VocabularyBank.applyTemplate(
                    template.getExplanation() != null ? template.getExplanation() : "",
                    template.getContent() != null ? template.getContent() : "",
                    code
            );
            resolvedLesson.setExplanation(firstNonBlank(resolved[0], template.getExplanation()));
            resolvedLesson.setContent(firstNonBlank(resolved[1], template.getContent()));

            String[] ansRes = VocabularyBank.applyTemplate(
                    "",
                    firstNonBlank(template.getCorrectAnswer(), ""),
                    code
            );
            resolvedLesson.setCorrectAnswer(firstNonBlank(ansRes[1], template.getCorrectAnswer()));
        }

        // Resolve options if JSON string exists
        if (template.getOptions() != null && !template.getOptions().equals("[]") && !template.getOptions().isBlank()) {
            try {
                String[] opts = mapper.readValue(template.getOptions(), String[].class);

                for (int i = 0; i < opts.length; i++) {
                    String[] optRes = VocabularyBank.applyTemplate("", opts[i], code);
                    opts[i] = firstNonBlank(optRes[1], opts[i]);
                }

                if ("SentenceOrder".equals(type) || "MultipleChoice".equals(type)) {
                    List<String> listOpts = new ArrayList<>(List.of(opts));
                    Collections.shuffle(listOpts);
                    opts = listOpts.toArray(new String[0]);
                }

                resolvedLesson.setOptions(mapper.writeValueAsString(opts));
            } catch (JsonProcessingException e) {
                resolvedLesson.setOptions(template.getOptions());
            }
        } else if ("FillInBlanks".equals(type)) {
            // Dynamic blank generation
            String content = firstNonBlank(resolvedLesson.getContent(), "");
            String[] words = content.split(" ");

            if (words.length > 2) {
                int blankIdx = random.nextInt(words.length);
                String hiddenWord = words[blankIdx].replaceAll("[^a-zA-Z\\u00C0-\\u024F¿?¡!]", "");

                if (!hiddenWord.isBlank()) {
                    words[blankIdx] = words[blankIdx].replace(hiddenWord, "___");
                    resolvedLesson.setContent(String.join(" ", words));
                    resolvedLesson.setCorrectAnswer(hiddenWord);

                    List<String> distractorPool = VocabularyBank.getAllTargetWords(code);
                    List<String> dynamicOpts = new ArrayList<>();
                    dynamicOpts.add(hiddenWord);

                    Collections.shuffle(distractorPool);
                    for (String dw : distractorPool) {
                        if (!dw.equalsIgnoreCase(hiddenWord) && dynamicOpts.size() < 4) {
                            dynamicOpts.add(dw);
                        }
                    }

                    Collections.shuffle(dynamicOpts);

                    try {
                        resolvedLesson.setOptions(
                                mapper.writeValueAsString(dynamicOpts.toArray(new String[0]))
                        );
                    } catch (Exception ignored) {
                        resolvedLesson.setOptions("[]");
                    }
                } else {
                    resolvedLesson.setOptions("[]");
                }
            } else {
                resolvedLesson.setOptions("[]");
            }
        } else {
            resolvedLesson.setOptions(firstNonBlank(template.getOptions(), "[]"));
        }

        // Last safety fallback
        if (isBlank(resolvedLesson.getContent())) {
            Lesson fallback = buildFallbackLesson(template.getLanguage(), template.getDifficulty());
            fallback.setId(template.getId());
            return fallback;
        }

        return resolvedLesson;
    }

    private boolean isUsableTemplate(Lesson lesson) {
        if (lesson == null) return false;

        String type = firstNonBlank(lesson.getType(), "Translate");
        String contentCandidate;

        if ("Translate".equalsIgnoreCase(type)) {
            contentCandidate = firstNonBlank(
                    lesson.getContent(),
                    lesson.getCorrectAnswer()
            );
        } else {
            contentCandidate = firstNonBlank(lesson.getContent(), "");
        }

        if (isBlank(contentCandidate)) return false;

        return !contentCandidate.contains("[Food]")
                && !contentCandidate.contains("[Verb]")
                && !contentCandidate.contains("[Family_Member]");
    }

    private Lesson buildFallbackLesson(Language lang, String difficulty) {
        Lesson lesson = new Lesson();
        lesson.setLanguage(lang);
        lesson.setType("Translate");
        lesson.setDifficulty(firstNonBlank(difficulty, "Beginner"));
        lesson.setTopic("Greetings");

        if ("es".equalsIgnoreCase(lang.getCode())) {
            lesson.setInstruction("Translate this greeting");
            lesson.setContent("Hola");
            lesson.setCorrectAnswer("Hello");
            lesson.setExplanation("Hola means Hello in Spanish.");
        } else if ("fr".equalsIgnoreCase(lang.getCode())) {
            lesson.setInstruction("Translate this greeting");
            lesson.setContent("Bonjour");
            lesson.setCorrectAnswer("Hello");
            lesson.setExplanation("Bonjour means Hello in French.");
        } else if ("de".equalsIgnoreCase(lang.getCode())) {
            lesson.setInstruction("Translate this greeting");
            lesson.setContent("Hallo");
            lesson.setCorrectAnswer("Hello");
            lesson.setExplanation("Hallo means Hello in German.");
        } else {
            lesson.setInstruction("Translate this word");
            lesson.setContent("Hello");
            lesson.setCorrectAnswer("Hello");
            lesson.setExplanation("Basic fallback lesson.");
        }

        lesson.setOptions("[]");
        return lesson;
    }

    private String inferInstruction(String type) {
        if ("MultipleChoice".equalsIgnoreCase(type)) return "Choose the correct answer";
        if ("FillInBlanks".equalsIgnoreCase(type)) return "Fill in the blank";
        if ("SentenceOrder".equalsIgnoreCase(type)) return "Arrange the sentence";
        if ("MatchMeaning".equalsIgnoreCase(type)) return "Match the correct meaning";
        return "Translate this word";
    }

    private String firstNonBlank(String... values) {
        if (values == null) return "";
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return "";
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}