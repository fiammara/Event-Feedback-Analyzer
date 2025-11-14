EventSync Fullstack Application

Overview
--------
EventSync is a fullstack application for managing events and collecting participant feedback, and automatically analyzing the feedback for sentiment.
It consists of a **Spring Boot backend** and a **React + Vite frontend**. Each submitted feedback is automatically analyzed for sentiment (positive, neutral, negative) using the **Hugging Face API** cardiffnlp/twitter-roberta-base-sentiment model.

Features
--------
- Create events (title + description)
- View all events and their feedback
- Submit feedback for a specific event
- Automatically receive feedback analysis from the Hugging Face API
- View feedback count and sentiment summary per event
- Interact with the app via the frontend web interface
- Access API documentation via **Swagger/OpenAPI**

Setup
-----
### Backend (Spring Boot)
1. Clone the repository:
   git clone https://github.com/fiammara/Event-Feedback-Analyzer.git
   cd eventsync/backend

2. Install dependencies and run:
   Make sure you have Java 17+ and Maven installed.
   mvn clean install
   mvn spring-boot:run

   - The backend will start on http://localhost:8080
   - H2 console available at http://localhost:8080/h2-console
     (JDBC URL: jdbc:h2:mem:testdb, user: sa, password: empty)

3. **Environment configuration**
   - A `.env.local` file is required for the Hugging Face API key:
     ```
     HUGGINGFACE_TOKEN=your_huggingface_api_key_here
     ```
   - Place it in the backend folder before running the app.

4. **Getting a Hugging Face API Token**
   1. Go to [https://huggingface.co](https://huggingface.co) and sign up for an account if you don’t have one.
   2. Click on your profile → **Settings** → **Access Tokens**.
   3. Click **New Token**, give it a name (e.g., `EventSync Backend`) and select **Write** permission.
   4. Copy the generated token.
   5. Paste it into the `.env` file as `HUGGINGFACE_TOKEN` (see step 3).

### Frontend (React + Vite)
1. Navigate to frontend directory:
   cd ../frontend

2. Install dependencies:
   npm install

3. Run the development server:
   npm run dev

   - The frontend will start on http://localhost:5173
   - It communicates with the backend for events and feedback

4. **Frontend environment configuration**
   - A `.env` file may be required for API URL:
     ```
     VITE_API_BASE=http://localhost:8080
     ```

### Running with Docker (Optional)
- The project includes a **Dockerfile** and **docker-compose.yml** for easy containerized deployment.

**Important: Environment Setup for Docker**
1. Create a `.env` file in the **root directory** (same location as `docker-compose.yml`):
   ```
   HUGGINGFACE_TOKEN=your_huggingface_api_key_here
   ```
   - You can copy `.env.example` as a template: `cp .env.example .env`
   - Replace `your_token_here` with your actual Hugging Face API token

2. Build and run the app using Docker:
   ```
   docker-compose up --build
   ```

3. Access the application:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui/index.html

- This runs both frontend and backend containers together, making it easy to run outside your local environment.
- The `.env` file is automatically loaded by Docker Compose and environment variables are passed to the containers.

API Endpoints (Backend)
----------------------
Base path: `/api/events`

1. `GET /api/events/{id}` — Get event by ID
2. `GET /api/events` — List all events
3. `POST /api/events` — Create a new event
4. `POST /api/events/{eventId}/feedback` — Add feedback to an event
5. `GET /api/events/{eventId}/feedback/summary` — Get feedback count and sentiment summary

- Swagger/OpenAPI documentation is available at `/swagger-ui.html` or `/swagger-ui/index.html` depending on Spring Boot version

Feedback summary
--------------------
- Each event might have a **feedback list** and a **summary section**.
- If no feedback has been submitted, the summary section is **empty**.
- Once feedback is added, the summary section **updates automatically**.
- The summary includes a **pie chart** showing the proportion of different sentiment types for that event.
- Sentiment analysis is performed automatically by the **Hugging Face API**, and the results are stored with each feedback entry.

Testing
-------
- Backend: unit and integration tests are implemented using test classes
- Frontend is not tested at this stage
- Swagger/OpenAPI can be used for backend API docs

Notes
-----
- Backend uses in-memory H2 database; data resets on restart
- For persistent storage, configure a real database in application.properties
- Frontend and backend communicate via REST API
- This project uses npm for frontend; Yarn references have been removed for clarity
- Application can run locally or in Docker containers for deployment

