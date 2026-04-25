package com.lingua.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingua.model.ChatMessage;
import com.lingua.model.Language;
import com.lingua.model.Lesson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${ai.enabled:true}")
    private boolean aiEnabled;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GeminiService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://generativelanguage.googleapis.com/v1beta").build();
        this.objectMapper = objectMapper;
    }

    public Lesson generateLesson(Language targetLanguage, String nativeLanguage, String difficulty) {
        if (difficulty == null || difficulty.isEmpty()) {
            difficulty = "Beginner";
        }
        String prompt = String.format(
                "Generate a language practice lesson for a %s speaker learning %s. " +
                "Difficulty level: %s. " +
                "Randomly pick ONE lesson type from: [Translate, FillInBlanks, MultipleChoice]. " +
                "Return ONLY a valid JSON object with the following exact keys: " +
                "\"type\" (string, the chosen type), " +
                "\"difficulty\" (string, %s), " +
                "\"instruction\" (string, clear instructions for the user in %s), " +
                "\"content\" (string, the sentence or phrase to practice in %s. If FillInBlanks, use '___' for the blank), " +
                "\"options\" (a JSON array of 3-4 string options if applicable, otherwise an empty array), " +
                "\"correctAnswer\" (string, the exact correct answer among the options or translation), " +
                "\"explanation\" (string, a brief explanation of grammar or vocabulary used in %s). " +
                "Do not include markdown formatting or extra text outside the JSON.",
                nativeLanguage, targetLanguage.getName(), difficulty, difficulty, nativeLanguage, targetLanguage.getName(), nativeLanguage);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("systemInstruction", Map.of("parts", List.of(Map.of("text", "You are an expert language teacher producing strict JSON API data."))));
        requestBody.put("contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", prompt)))));
        requestBody.put("generationConfig", Map.of("temperature", 0.7));

        try {
            String response = webClient.post()
                    .uri("/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            String content = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

            // clean up markdown
            content = content.replaceAll("^```json\\s*", "").replaceAll("^```\\s*", "").replaceAll("```$", "").trim();

            JsonNode lessonNode = objectMapper.readTree(content);

            Lesson lesson = new Lesson();
            lesson.setLanguage(targetLanguage);
            lesson.setType(lessonNode.path("type").asText("Translate"));
            lesson.setDifficulty(lessonNode.path("difficulty").asText(difficulty));
            lesson.setInstruction(lessonNode.path("instruction").asText("Translate this sentence"));
            lesson.setContent(lessonNode.path("content").asText(""));
            lesson.setCorrectAnswer(lessonNode.path("correctAnswer").asText(""));
            lesson.setExplanation(lessonNode.path("explanation").asText(""));
            
            JsonNode optionsNode = lessonNode.path("options");
            if (optionsNode.isArray()) {
                lesson.setOptions(optionsNode.toString());
            } else {
                lesson.setOptions("[]");
            }

            return lesson;
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.TooManyRequests e) {
            System.err.println("Gemini API Rate Limit Exceeded (429) for Lesson Generation.");
            Lesson fallback = new Lesson();
            fallback.setLanguage(targetLanguage);
            fallback.setType("Translate");
            fallback.setDifficulty(difficulty);
            fallback.setInstruction("Translate the following");
            fallback.setContent("The AI is currently resting to prevent overheating. Please wait a few moments before continuing.");
            fallback.setCorrectAnswer("I will wait");
            fallback.setOptions("[\"I will wait\"]");
            fallback.setExplanation("Rate limit exceeded (429 Too Many Requests).");
            return fallback;
        } catch (Exception e) {
            System.err.println("Gemini API call failed for Lesson Generation. Producing fallback.");
            e.printStackTrace();
            Lesson fallback = new Lesson();
            fallback.setLanguage(targetLanguage);
            fallback.setType("Translate");
            fallback.setDifficulty(difficulty);
            fallback.setInstruction("Translate the following");
            fallback.setContent("Error generating lesson");
            fallback.setCorrectAnswer("Error");
            fallback.setOptions("[\"Error\"]");
            fallback.setExplanation("An error occurred during generation.");
            return fallback;
        }
    }

    public com.lingua.dto.AdvancedChatResponse generateChatResponse(String targetLanguage, String nativeLanguage, String level, String userMessage, List<ChatMessage> history, String scenario) {
        List<Map<String, Object>> contents = new ArrayList<>();

        int start = Math.max(0, history.size() - 6);
        boolean userMsgAppended = false;
        for (int i = start; i < history.size(); i++) {
            ChatMessage msg = history.get(i);
            String role = msg.getRole().equalsIgnoreCase("user") ? "user" : "model";
            contents.add(Map.of("role", role, "parts", List.of(Map.of("text", msg.getContent()))));
            if (i == history.size() - 1 && role.equals("user") && msg.getContent().equals(userMessage)) {
                userMsgAppended = true;
            }
        }
        if (!userMsgAppended) {
            contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", userMessage))));
        }

        if (!aiEnabled) {
            return new com.lingua.dto.AdvancedChatResponse(
                    "(Offline Mode) Mock response mimicking the target language.",
                    "This is a mocked translation simulating a backend without live AI dependency.",
                    "No grammar tips available offline.",
                    "",
                    "Next action"
            );
        }

        Map<String, Object> requestBody = new HashMap<>();
        String roleplayConstraint = "";
        if (scenario != null && !scenario.isEmpty()) {
            roleplayConstraint = String.format("ROLEPLAY SCENARIO: You are participating in a roleplay scenario set at a %s. Act completely in-character. Do not break character. Respond as an immersive partner.", scenario.toUpperCase());
        }

        String sysPrompt = String.format("You are an AI language tutor inside a language learning platform.\n" +
                "%s\n\n" +
                "Your role is to help the user learn a target language while using their native language for support.\n" +
                "\n" +
                "Rules:\n" +
                "1. The user's native language is: %s\n" +
                "2. The target language being learned is: %s\n" +
                "3. The user's proficiency level is: %s\n" +
                "4. Use the target language for examples, conversation practice, and exercises.\n" +
                "5. Use the native language for explanations, hints, corrections, grammar notes, and translations.\n" +
                "6. If the user makes mistakes, correct them gently and explain the correction in the native language.\n" +
                "7. Keep the tone encouraging, interactive, and educational.\n" +
                "8. Prefer short, structured responses.\n" +
                "9. When useful, provide:\n" +
                "   - target language sentence\n" +
                "   - native language meaning\n" +
                "   - grammar explanation\n" +
                "   - suggested reply\n" +
                "10. Adapt difficulty based on the user’s performance.\n" +
                "\n" +
                "Always return valid JSON only in this format:\n" +
                "{\n" +
                "  \"replyInTargetLanguage\": \"\",\n" +
                "  \"translationInNativeLanguage\": \"\",\n" +
                "  \"grammarTipInNativeLanguage\": \"\",\n" +
                "  \"correctionInNativeLanguage\": \"\",\n" +
                "  \"suggestedReply\": \"\"\n" +
                "}", roleplayConstraint, nativeLanguage, targetLanguage, level);

        requestBody.put("systemInstruction", Map.of("parts", List.of(Map.of("text", sysPrompt))));
        requestBody.put("contents", contents);
        requestBody.put("generationConfig", Map.of(
                "temperature", 0.7, 
                "maxOutputTokens", 1000,
                "responseMimeType", "application/json"
        ));

        try {
            String response = webClient.post()
                    .uri("/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            String content = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            content = content.replace("```json", "").replace("```", "").trim();

            JsonNode chatNode = objectMapper.readTree(content);
            return new com.lingua.dto.AdvancedChatResponse(
                    chatNode.path("replyInTargetLanguage").asText(""),
                    chatNode.path("translationInNativeLanguage").asText(""),
                    chatNode.path("grammarTipInNativeLanguage").asText(""),
                    chatNode.path("correctionInNativeLanguage").asText(""),
                    chatNode.path("suggestedReply").asText("")
            );
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.TooManyRequests e) {
            System.err.println("Gemini API Rate Limit Exceeded (429) for Chat.");
            return new com.lingua.dto.AdvancedChatResponse(
                    "I am receiving too many messages right now. Please give me a brief moment to catch my breath.",
                    "Rate limit exceeded.",
                    "",
                    "Please wait a few seconds before trying again.",
                    "I understand. Take a break."
            );
        } catch (Exception e) {
            System.err.println("Gemini API call failed for Chat.");
            e.printStackTrace();
            return new com.lingua.dto.AdvancedChatResponse(
                    "Sorry, I had an issue connecting to my brain.",
                    "API error occurred.",
                    "",
                    "Something went wrong while generating the response.",
                    "Try again later."
            );
        }
    }

    public String evaluateScenario(String targetLanguage, String nativeLanguage, String level, List<ChatMessage> history) {
        List<Map<String, Object>> contents = new ArrayList<>();
        for (ChatMessage msg : history) {
            String role = msg.getRole().equalsIgnoreCase("user") ? "user" : "model";
            contents.add(Map.of("role", role, "parts", List.of(Map.of("text", msg.getContent()))));
        }

        Map<String, Object> requestBody = new HashMap<>();
        String sysPrompt = String.format("Analyze the provided conversation history between a %s user (proficiency: %s) and an AI tutor roleplaying in %s.\n" +
                "Evaluate the user's performance and output ONLY valid JSON in this exact format:\n" +
                "{\n" +
                "  \"fluencyScore\": <int 0-100>,\n" +
                "  \"grammarIssues\": [\"issue 1\", \"issue 2\"],\n" +
                "  \"suggestedVocabulary\": [\"word1: translation\", \"word2: translation\"],\n" +
                "  \"overallFeedback\": \"Brief encouraging feedback in %s\"\n" +
                "}", nativeLanguage, level, targetLanguage, nativeLanguage);

        requestBody.put("systemInstruction", Map.of("parts", List.of(Map.of("text", sysPrompt))));
        requestBody.put("contents", contents);
        requestBody.put("generationConfig", Map.of("temperature", 0.3, "responseMimeType", "application/json"));

        try {
            String response = webClient.post()
                    .uri("/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            String content = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            return content.replace("```json", "").replace("```", "").trim();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.TooManyRequests e) {
            System.err.println("Gemini API Rate Limit Exceeded (429) for Scenario Evaluation.");
            return "{\"fluencyScore\": 0, \"grammarIssues\": [\"Rate limit exceeded\"], \"suggestedVocabulary\": [], \"overallFeedback\": \"Evaluation failed due to too many requests. Please try again later.\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"fluencyScore\": 0, \"grammarIssues\": [\"Error evaluating scenario\"], \"suggestedVocabulary\": [], \"overallFeedback\": \"Evaluation failed.\"}";
        }
    }

    public String generateDailyChallenge(Language targetLanguage, String nativeLanguage) {
        String prompt = String.format("Generate a unique daily multiple-choice challenge (vocabulary, grammar, or culture) for a %s speaker learning %s.\n" +
                "Output ONLY valid JSON in this exact format:\n" +
                "{\n" +
                "  \"question\": \"The question in %s or %s\",\n" +
                "  \"options\": [\"Option 1\", \"Option 2\", \"Option 3\", \"Option 4\"],\n" +
                "  \"correctAnswer\": \"The exact matching correct option\",\n" +
                "  \"explanation\": \"Brief explanation in %s\"\n" +
                "}", nativeLanguage, targetLanguage.getName(), nativeLanguage, targetLanguage.getName(), nativeLanguage);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("systemInstruction", Map.of("parts", List.of(Map.of("text", "You are an expert language teacher producing strict JSON API data."))));
        requestBody.put("contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", prompt)))));
        requestBody.put("generationConfig", Map.of("temperature", 0.9, "responseMimeType", "application/json"));

        try {
            String response = webClient.post()
                    .uri("/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            String content = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            return content.replace("```json", "").replace("```", "").trim();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.TooManyRequests e) {
            System.err.println("Gemini API Rate Limit Exceeded (429) for Daily Challenge.");
            return "{\"question\": \"The AI is currently resting due to high traffic. What should you do?\", \"options\": [\"Wait patiently\", \"Panic\", \"Close the app\", \"Refresh 100 times\"], \"correctAnswer\": \"Wait patiently\", \"explanation\": \"API rate limit exceeded (429). Please try again shortly.\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"question\": \"What is the most important part of language learning?\", \"options\": [\"Practice\", \"Sleep\", \"Eating\", \"Running\"], \"correctAnswer\": \"Practice\", \"explanation\": \"Consistent practice is the key to mastering any language.\"}";
        }
    }
}
