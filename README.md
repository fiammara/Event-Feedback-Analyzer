
# EventSync Fullstack Application

### Live Demo (For Users)

 [https://frontend-ic47.onrender.com](https://frontend-ic47.onrender.com)
> **Note:** The app may take a few minutes to load because it’s running on a free-tier hosting plan. Thanks for your patience!

### What You Can Do

- Create and view events
- Submit feedback for events
- Automatically get sentiment analysis of the feedback (positive, neutral, negative)
- View feedback counts and sentiment summaries per event

---

### The App For Managing Events With AI-Driven Sentiment Insights

EventSync is a fullstack application for managing events and collecting participant feedback. Each feedback is automatically analyzed for sentiment using the Hugging Face `cardiffnlp/twitter-roberta-base-sentiment` AI model.

**Tech Stack:**
- Backend: **Spring Boot**
- Frontend: **React + Vite**
- Database: **H2**
- Sentiment analysis via **Hugging Face API**

---

### Feedback Summary

- Each event has a **feedback list** and **summary section**
- Summary updates automatically when feedback is added
- Includes **pie chart** for sentiment statistics

---
# Setup for developers

Important:
Before running/testing the application, create the following files:

1. **Backend `.env`** (at the root of the repository)

```env
HUGGINGFACE_TOKEN=your_secret_token_here
```

2. **Backend `application-token.properties`** (root or backend folder) — **same content as `.env`**

```properties
HUGGINGFACE_TOKEN=your_secret_token_here
```

3. **Frontend `env.local`** (inside `frontend` folder)

```env
VITE_API_BASE=http://localhost:8080
```

### How to Get the Hugging Face Token

1. Go to [Hugging Face](https://huggingface.co) and create an account.
2. Click your profile → **Settings → Access Tokens**.
3. Click **New Token**, give it a name (e.g., `sentiment`), and select **Write** permission.
4. Copy the token and place it in `.env` and `application-token.properties` as shown above.

---

## Setting Up and Running the App Locally

### Backend (Spring Boot)

1. Clone the repository:

```bash
git clone https://github.com/fiammara/Event-Feedback-Analyzer.git
```

2. Build and run the backend:

```bash
cd eventsync
mvn clean install
mvn spring-boot:run
```

- Backend runs at: `http://localhost:8080`
- H2 console: `http://localhost:8080/h2-console`  
  (JDBC URL: `jdbc:h2:mem:testdb`, user: `sa`, password: empty)

---

### Frontend (React + Vite)

1. Navigate to frontend:

```bash
cd frontend
```

2. Install dependencies:

```bash
npm install
```

3. Run development server:

```bash
npm run dev
```

- Frontend runs at: `http://localhost:5173`
- Uses backend URL from `env.local`

---

## Optional: Local Docker Setup

1. Ensure `.env` file exists in the project root and contains the line HUGGINGFACE_TOKEN= with your token value.
2. Build and run containers:

```bash
docker-compose up --build
```

3. Access the app:

`http://localhost:3000`

---

## API Endpoints

Base path: `/api/events`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/events` | List all events |
| GET    | `/api/events/{id}` | Get event by ID |
| POST   | `/api/events` | Create new event |
| POST   | `/api/events/{eventId}/feedback` | Submit feedback for an event |
| GET    | `/api/events/{eventId}/feedback/summary` | Feedback count & sentiment summary |

- Swagger/OpenAPI docs: `/swagger-ui.html` or `/swagger-ui/index.html`

---

## Notes

- Backend uses **in-memory H2 database**; data resets on restart
- For persistent storage, configure a real database in `application.properties`
- Frontend and backend communicate via REST API
- Uses npm for frontend
- CI workflow builds backend and frontend automatically; deployment is **manual**  
