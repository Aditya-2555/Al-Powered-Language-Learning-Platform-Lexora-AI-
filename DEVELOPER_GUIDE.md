# 🚀 Lexora AI (Lingua) Platform - Complete Developer Guide

Welcome to the comprehensive internal documentation for the **Lexora AI (Lingua) Platform**. This document is tailored specifically to the complete scope of the project, including all recent modernizations, spaced-repetition models, daily goals, scenario sandbox, and lesson hybrid generation. It covers file structures, logic flows, features step-by-step, and debugging strategies.

---

## 1. 🏗️ High-Level Architecture & Tech Stack

Our application is a **Full-Stack SaaS Web App** built around a modern, decoupled architecture designed for a polished language-learning experience.

### The Stack
*   **Frontend**: React (Vite), React Router DOM, Vanilla CSS styling (focus on premium SaaS aesthetics, glassmorphism, dynamic animations), Axios. Runs on `http://localhost:5173`.
*   **Backend**: Spring Boot 3 (Java 17+), Spring Data JPA, RESTful Architecture. Runs on `http://localhost:8080`.
*   **Database**: MySQL.
*   **AI Integration**: Google Gemini API (via `GeminiService.java`).

### The Big Picture Request Flow
1. A user interacts with a React screen (`/dashboard`, `/scenarios`, etc.).
2. The React component makes an asynchronous call via `axios` (intercepted to auto-unwrap `{success, data, message}` DTOs) to our Spring Boot Backend.
3. The requests hit the designated **Controller** in Spring (e.g., `LessonController`).
4. The Controller delegates heavy lifting to a **Service** (e.g., `LessonService`).
5. The Service utilizes core logic, fetches specific seeded data (`VocabularyBank`), or calls the AI via `GeminiService` if generative content is needed.
6. The Service reads/writes to MySQL via a **Repository** (e.g., `UserRepository`).
7. Data flows back out through the Controller to React to trigger a state update and visual animation.

---

## 2. 📁 Project Structure Detailed Breakdown

### A. Frontend (`C:\Project\frontend\`)
The frontend is built with an emphasis on seamless, modern UI/UX design.

*   `src/App.jsx`: **The Core Router**. Manages user state via `localStorage`, defines protected routes, and holds the global Axios response interceptor for unified response handling.
*   **Major View Pages (`src/pages/`)**:
    *   `Landing.jsx` & `Home.jsx`: Public-facing landing page and authentication/entry gateway.
    *   `LanguageSetup.jsx`: User onboarding for setting up target & native languages.
    *   `Dashboard.jsx`: The central user hub featuring XP, Daily Goals, Daily Challenge, Weekly Activity Pulse and quick links.
    *   `Lesson.jsx`: The core learning UI for generated hybrid lessons.
    *   `VocabularyNotebook.jsx`: The Spaced-Repetition System (SRS) UI showing words due for review and learned vocabulary.
    *   `Scenarios.jsx`: Sandbox roleplay interaction where users pick situations (e.g., Cafe, Airport) and converse with an AI acting within strict boundaries.
    *   `SpeakingPractice.jsx`: Voice-recognition practice utilizing the browser's Web Speech API.
    *   `Tutor.jsx`: The direct AI tutor chat interface for conversational learning and granular explanations.
    *   `AnalyticsDashboard.jsx` & `MistakesReview.jsx`: Deep dive into topic mastery, performance history, and past errors.
    *   `AdminDashboard.jsx`: System-wide metrics for admins.

### B. Backend (`C:\Project\backend\`)
The backend strictly adheres to a layered **Controller -> Service -> Repository -> Model** architecture.

*   `model/`: JPA Entities mapping to MySQL tables (`User`, `Lesson`, `VocabularyEntry`, `DailyGoal`, `TopicPerformance`, `Achievement`, `ChatSession`, etc.).
*   `dto/`: Data Transfer Objects (e.g., `RecommendationDTO`, `DashboardStatsDTO`) ensuring controlled API responses.
*   `repository/`: Spring Data JPA interfaces for seamless DB transactions.
*   `controller/`: REST API endpoints orchestrating data between frontend and services.
*   `service/`: The "Brains" of the platform handling business logic. Includes:
    *   `ProgressService.java`: Calculates XP, handles streaks, unlocks achievements.
    *   `LessonService.java`: Generates lessons (hybrid config).
    *   `VocabularyService.java`: Spaced repetition logic.
    *   `GeminiService.java`: Central pipeline for Google Gemini prompt building and invocation.
    *   `VocabularyBank.java`: Local seeded asset component.

---

## 3. 🧠 Core Modules & Step-by-Step Logic Explained

### A. Dynamic Lesson Content System (`LessonService` & `Lesson.jsx`)
We recently migrated from purely AI-generated lessons to a **Hybrid Dynamic Template System** for stability and anti-repetition.
1.  **Trigger**: User selects a topic, triggering `POST /api/lessons/generate`.
2.  **Assembly**: `LessonService` consults the local `VocabularyBank` to fetch seeded words/sentences for that specific topic (e.g. Travel, Food).
3.  **Template Generation**: The backend injects the vocabulary into set exercise templates (Multiple Choice, Fill in Blank, Order Sentence, Translate) using dynamic substitution.
4.  **Tracking & Stability**: `TopicPerformance` history is checked to prevent showing the exact same questions repeatedly. The Gemini API serves strictly as a fallback or for dynamic distractor generation to reduce AI hallucination risks.
5.  **Completion**: Upon sending `POST /api/lessons/submit`, `ProgressService` processes the score, awards XP, logs mistakes for later review, and generates the *Next Recommended Topic*.

### B. Spaced-Repetition Vocabulary Notebook (`VocabularyService` & `VocabularyNotebook.jsx`)
A robust SRS (Spaced Repetition System) like Anki/Duolingo.
1.  **Data Structure**: The `VocabularyEntry` model tracks `revisionCount`, `lastRevisedAt`, and `nextRevisionDue`.
2.  **Insertion Point**: When a user completes a lesson or encounters new words in chat, they are saved as new Vocabulary Entries for that user.
3.  **The SRS Algorithm**:
    *   **Success (I Remembered)**: `revisionCount` increments. Next due date extends exponentially (+1 day, +3 days, +7 days, +14 days).
    *   **Failure (I Forgot)**: `revisionCount` drops to 0, and the item becomes due tomorrow (+1 day).
4.  **Frontend View**: The UI filters entries on the fly. The "Review Due" action specifically isolates entries where `nextRevisionDue` is today or earlier.

### C. Scenario Roleplay Sandbox (`TutorService` & `Scenarios.jsx`)
1.  **Selection**: User selects a predefined persona scenario (e.g., "Angry Waiter in Paris").
2.  **Prompt Injection**: `GeminiService` prepares a highly restrictive system prompt forcing the AI to strictly adhere to the role, user's skill level, and target language, and importantly—NOT break character.
3.  **Live Grading (Performance Review)**: During or at the end of the scenario, the backend evaluates user conversation turns for flow and accuracy, returning a specialized metric summary.

### D. Speaking / Pronunciation Module (`SpeakingPractice.jsx`)
1.  **Frontend Audio Capture**: React natively utilizes `webkitSpeechRecognition` (Web Speech API) to capture microphone data, rendering a real-time transcript.
2.  **Backend Verification**: The generated transcript is sent to `SpeakingController` to be graded using cosine similarity, string matching algorithms, or AI evaluation against the target sentence, returning a feedback score.

### E. Gamification, Weekly Activity, & Goals (`ProgressService`)
1.  **Daily Goals**: Nightly/daily system checks create transient `DailyGoal` entities (e.g., "Complete 2 Lessons").
2.  **XP & Streaks**: Actions distribute XP. Logging in triggers streak consistency checks.
3.  **Weekly Activity Pulse**: React frontend parses `LearningActivity` timestamps to visually populate the activity heat-map for the last 7 days.

---

## 4. 🪲 Comprehensive Debugging Guide

### Common Problem A: Blank Screen / React Errors
*   **Symptoms**: White screen, buttons unresponsive, "Uncaught TypeError".
*   **Fix**:
    1.  Open Chrome DevTools (`F12`) -> **Console**.
    2.  Errors like `Cannot read properties of null (reading 'map')` imply a component rendered before an API call finished. Add conditional rendering `if (!data) return <LoadingSpinner />;` or use optional chaining `data?.map()`.

### Common Problem B: Network Errors / JSON Unwrapping
*   **Symptoms**: "Network Error" on UI, 404/500 in Network tab.
*   **Fix**:
    1.  Check the **Network** tab in DevTools.
    2.  `404`: Typo in frontend `axios.get('/api/...')` vs backend `@GetMapping("/api/...")`.
    3.  `CORS Error`: Ensure `WebConfig` or `@CrossOrigin` allows local access on the Spring backend.
    4.  **Note on Global Interceptor**: Our `App.jsx` automatically unwraps backend payload envelopes (`{success: true, data: {}}`). If you access `response.data.data` inside a component, it will be `undefined`; just use `response.data`.

### Common Problem C: Backend Exceptions
*   **Symptoms**: Postman / Dev tools return 500 Server Error.
*   **Fix**:
    1.  Look at the `mvn spring-boot:run` console log.
    2.  A `NullPointerException`: Find the first line referencing `com.lingua`. A database property that is expected wasn't injected or found.
    3.  A `@Valid` or Constraint Violation error: Data passed from Frontend violates `@NotNull` or length on your JPA Entity.

### Common Problem D: AI / Gemini Hallucinations
*   **Symptoms**: AI returns plain text instead of JSON, or breaks character in Scenarios.
*   **Fix**:
    1.  Go to `LessonService` or `TutorService` and strictly modify the prompt.
    2.  You *must* be explicit: `"RETURN ONLY VALID JSON. WITHOUT MARKDOWN BLOCK. DO NOT WRAP."`
    3.  If parsing fails, check where `ObjectMapper` reads the Gemini response and implement robust JSON extraction (trimming ` ```json ` ).

---

## 5. 🛠 The Developer Feature Addition Walkthrough

To add a new feature (e.g., "Quizzes"):

1.  **Database Entity**: Create `model/Quiz.java` (annotate with `@Entity`).
2.  **Repository Level**: Create `repository/QuizRepository.java` extending `JpaRepository`.
3.  **Data Transfer (DTO)**: Create `dto/QuizResponseDTO.java`.
4.  **Business Logic (Service)**: Create `service/QuizService.java`. Inject Repositories here. Do math / AI calls here.
5.  **API Surface (Controller)**: Create `controller/QuizController.java` with `@RestController`. Route to service.
6.  **Frontend View**: Create `pages/Quizzes.jsx` (Use high-quality UI assets, gradient borders, neat typography).
7.  **Routing**: Add `<Route path="/quizzes" element={<Quizzes />} />` in `App.jsx`.
8.  **API Calling**: In `Quizzes.jsx`, `useEffect(() => { axios.get('/api/quizzes') ... }, []);`

Follow these architectural rules, and the Lexora backend will continue to be a stable foundation for the project!
