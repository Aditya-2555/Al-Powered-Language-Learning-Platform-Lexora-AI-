package com.lingua.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingua.model.Language;
import com.lingua.model.Lesson;
import com.lingua.repository.LanguageRepository;
import com.lingua.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LessonSeeder implements CommandLineRunner {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LanguageRepository languageRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        if (lessonRepository.count() > 0) {
            return; // Already seeded
        }

        System.out.println("Seeding database with massive Lesson Templates...");

        List<Language> languages = languageRepository.findAll();
        for (Language lang : languages) {
            String code = lang.getCode().toLowerCase();
            if (!code.equals("es") && !code.equals("fr") && !code.equals("de")) continue;

            List<Lesson> batch = new ArrayList<>();

            // ============================================
            // TOPIC: Greetings & Basics
            // ============================================
            if (code.equals("es")) {
                batch.add(createLesson(lang, "Translate", "Beginner", "Greetings", "Translate to English", "", "Buenos días", "[]", "Good morning"));
                batch.add(createLesson(lang, "Translate", "Beginner", "Greetings", "Translate this greeting", "", "Hola, ¿cómo estás?", "[]", "Hello, how are you?"));
                batch.add(createLesson(lang, "FillInBlanks", "Beginner", "Greetings", "Select the right greeting", "___ tardes.", "Buenas", "[\"Buenos\", \"Buenas\", \"Buena\", \"Bueno\"]", "Tardes is feminine plural."));
                batch.add(createLesson(lang, "MultipleChoice", "Beginner", "Greetings", "How to say 'Goodbye'?", "___", "Adiós", "[\"Hola\", \"Gracias\", \"Adiós\", \"Por favor\"]", "Adiós means goodbye."));
                batch.add(createLesson(lang, "SentenceOrder", "Intermediate", "Greetings", "Order the sentence", "you / how / are", "cómo estás tú", "[\"estás\", \"cómo\", \"tú\"]", "Question word goes first: cómo."));
                batch.add(createLesson(lang, "MultipleChoice", "Beginner", "Greetings", "Select the translation for 'Please'", "___", "Por favor", "[\"Por favor\", \"Gracias\", \"Sí\", \"De nada\"]", "Basic polite terms"));
            } else if (code.equals("fr")) {
                batch.add(createLesson(lang, "Translate", "Beginner", "Greetings", "Translate to English", "", "Bonjour", "[]", "Hello"));
                batch.add(createLesson(lang, "FillInBlanks", "Beginner", "Greetings", "Select the right greeting", "___ nuit.", "Bonne", "[\"Bon\", \"Bonne\", \"Bons\", \"Bonnes\"]", "Nuit is feminine."));
                batch.add(createLesson(lang, "MultipleChoice", "Beginner", "Greetings", "How to say 'Thank you'?", "___", "Merci", "[\"Oui\", \"S'il vous plaît\", \"Merci\", \"De rien\"]", "Merci means thank you."));
                batch.add(createLesson(lang, "SentenceOrder", "Intermediate", "Greetings", "Order the sentence", "are / you / how", "comment allez vous", "[\"vous\", \"comment\", \"allez\"]", "Standard formal greeting."));
                batch.add(createLesson(lang, "MultipleChoice", "Beginner", "Greetings", "Select the translation for 'Please'", "___", "S'il vous plaît", "[\"S'il vous plaît\", \"Merci\", \"Oui\", \"De rien\"]", "Basic polite terms"));
            } else if (code.equals("de")) {
                batch.add(createLesson(lang, "Translate", "Beginner", "Greetings", "Translate to English", "", "Guten Morgen", "[]", "Good morning"));
                batch.add(createLesson(lang, "FillInBlanks", "Beginner", "Greetings", "Select the right greeting", "___ Nacht.", "Gute", "[\"Guten\", \"Gute\", \"Guter\", \"Gutes\"]", "Nacht is feminine."));
                batch.add(createLesson(lang, "MultipleChoice", "Beginner", "Greetings", "How to say 'Please'?", "___", "Bitte", "[\"Danke\", \"Hallo\", \"Tschüss\", \"Bitte\"]", "Bitte means please."));
                batch.add(createLesson(lang, "SentenceOrder", "Intermediate", "Greetings", "Order the sentence", "are / you / how", "wie geht es dir", "[\"es\", \"dir\", \"wie\", \"geht\"]", "Standard informal greeting."));
                batch.add(createLesson(lang, "MultipleChoice", "Beginner", "Greetings", "Select the translation for 'Please'", "___", "Bitte", "[\"Bitte\", \"Danke\", \"Ja\", \"Tschüss\"]", "Basic polite terms"));
            }

            // ============================================
            // TOPIC: Vocabulary Templates (Dynamic)
            // ============================================
            if (code.equals("es")) {
                batch.add(createLesson(lang, "Translate", "Beginner", "Vocabulary", "Translate this word", "", "[Food]", "[]", "[Food]"));
                batch.add(createLesson(lang, "Translate", "Beginner", "Vocabulary", "Translate to English", "", "Mi [Family_Member] está aquí.", "[]", "My [Family_Member] is here."));
                batch.add(createLesson(lang, "SentenceOrder", "Intermediate", "Vocabulary", "Reorder the words", "I like [Food]", "Me gusta el [Food]", "[\"el\", \"Me\", \"gusta\", \"[Food]\"]", "Reflexive ordering."));
                batch.add(createLesson(lang, "FillInBlanks", "Intermediate", "Vocabulary", "Fill in the missing word", "I [Verb] and eat [Food].", "Yo voy a [Verb] y comer [Food]", "[]", "Dynamic generation"));
            } else if (code.equals("fr")) {
                batch.add(createLesson(lang, "Translate", "Beginner", "Vocabulary", "Translate this word", "", "[Food]", "[]", "[Food]"));
                batch.add(createLesson(lang, "Translate", "Beginner", "Vocabulary", "Translate to English", "", "Mon [Family_Member] est icí.", "[]", "My [Family_Member] is here."));
                batch.add(createLesson(lang, "SentenceOrder", "Intermediate", "Vocabulary", "Reorder the words", "I love [Food]", "J'aime le [Food]", "[\"le\", \"J'aime\", \"[Food]\"]", "Ordering check."));
                batch.add(createLesson(lang, "FillInBlanks", "Intermediate", "Vocabulary", "Fill in the missing word", "I [Verb] and eat [Food].", "Je vais [Verb] et manger [Food]", "[]", "Dynamic generation"));
            } else if (code.equals("de")) {
                batch.add(createLesson(lang, "Translate", "Beginner", "Vocabulary", "Translate this word", "", "[Food]", "[]", "[Food]"));
                batch.add(createLesson(lang, "Translate", "Beginner", "Vocabulary", "Translate to English", "", "Mein [Family_Member] ist hier.", "[]", "My [Family_Member] is here."));
                batch.add(createLesson(lang, "SentenceOrder", "Intermediate", "Vocabulary", "Reorder the words", "I eat [Food]", "Ich esse [Food]", "[\"esse\", \"Ich\", \"[Food]\"]", "SVO check."));
                batch.add(createLesson(lang, "FillInBlanks", "Intermediate", "Vocabulary", "Fill in the missing word", "I [Verb] and eat [Food].", "Ich werde [Verb] und [Food] essen.", "[]", "Dynamic generation"));
            }

            // Save to DB
            lessonRepository.saveAll(batch);
        }

        System.out.println("Lesson seeding completed successfully!");
    }

    private Lesson createLesson(Language lang, String type, String difficulty, String topic, String instruction, String content, String correctAnswer, String optionsJson, String explanation) {
        return new Lesson(lang, type, difficulty, topic, instruction, explanation, content, optionsJson, correctAnswer);
    }
}
