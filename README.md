# MynoAno

MynoAno is a location-based social discovery platform designed to help people connect, interact, and discover activities around them.

It offers two social experiences: **MYNO**, for public social discovery, and **ANO**, for more private interactions with a trusted circle.

## Live Demo

- **Frontend:** https://mynoano.vercel.app
- **Backend:** https://mynoano.onrender.com

## Features

- **MYNO:** Discover people and connect through public profiles.
- **ANO:** Interact within a private circle.
- **Nearby Discovery:** Discover people based on location.
- **Events:** Create, explore, and participate in events.
- **Tea:** Share posts and interact with community discussions.
- **Messaging:** Communicate with other users.
- **Friendships:** Send and manage connection requests.
- **Safety:** Privacy controls, blocking, and reporting.
- **Authentication:** Account registration and OTP verification.

## Tech Stack

**Frontend**
- React
- JavaScript
- Vite
- React Router

**Backend**
- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT Authentication
- Maven

**Database**
- PostgreSQL
- Neon

**Deployment**
- Vercel — Frontend
- Render — Backend
- Neon — Database

## Project Structure

```text
MynoAno/
├── backend/
│   ├── src/
│   └── pom.xml
├── frontend/
│   ├── src/
│   ├── public/
│   └── package.json
└── docker-compose.yml
```

## Getting Started

### Prerequisites

- Java 17
- Maven
- Node.js and npm
- PostgreSQL

### 1. Clone the Repository

```bash
git clone https://github.com/madhurameenakshi0195/MynoAno.git
cd MynoAno
```

### 2. Backend Setup

```bash
cd backend
mvn clean install
```

Configure the database connection and application environment variables.

Run the backend:

```bash
mvn spring-boot:run
```

Backend runs at:

```text
http://localhost:8080
```

### 3. Frontend Setup

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at:

```text
http://localhost:5173
```

## Environment Variables

Configure the required environment variables for your deployment. Never commit credentials or secret keys to GitHub.

| Variable | Purpose |
|---|---|
| `DB_URL` | PostgreSQL database connection |
| `CORS_ORIGIN` | Allowed frontend origin |
| `VITE_API_BASE_URL` | Backend API base URL |
| `OTP_DEV_CODE` | Optional development OTP configuration |

## Author

**Madhura Meenakshi**

[GitHub](https://github.com/madhurameenakshi0195)
 
