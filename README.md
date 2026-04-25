# Lexora AI

Lexora AI is a full-stack language-learning platform with a React/Vite frontend and a Spring Boot backend. The app includes lessons, vocabulary review, scenario roleplay, speaking practice, tutor chat, analytics, daily goals, and admin views.

## Tech Stack

- Frontend: React, Vite, React Router DOM, Axios, Lucide React
- Backend: Java 17, Spring Boot 3, Spring Web, WebFlux, Spring Data JPA, Validation
- Database: MySQL
- AI: Google Gemini integration through the backend

## Project Structure

```text
.
|-- backend/              # Spring Boot API and business logic
|-- frontend/             # React/Vite web app
|-- DEVELOPER_GUIDE.md    # Detailed internal architecture and debugging guide
|-- .gitignore            # Combined ignore rules for the whole project
`-- README.md             # Project overview and setup
```

## Prerequisites

- Node.js and npm
- Java 17+
- Maven
- MySQL

## Backend Setup

1. Create or start a local MySQL server.
2. Confirm the database settings in `backend/src/main/resources/application.properties`.
3. Start the backend:

```bash
cd backend
mvn spring-boot:run
```

The backend runs on `http://localhost:8080` by default.

## Frontend Setup

Install dependencies and start the Vite dev server:

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on `http://localhost:5173` by default.

## Common Commands

```bash
# Frontend
cd frontend
npm run dev
npm run build
npm run lint
npm run preview

# Backend
cd backend
mvn spring-boot:run
mvn test
```

## Configuration Notes

- The frontend expects the backend to be available at `http://localhost:8080`.
- The backend CORS configuration allows `http://localhost:5173`.
- MySQL is configured for a local `lingua_db` database.
- Keep real API keys, database passwords, and local overrides out of Git. Prefer environment variables or local-only properties files for private configuration.

## More Documentation

See `DEVELOPER_GUIDE.md` for deeper architecture notes, feature flows, debugging steps, and guidance for adding new modules.
