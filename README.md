# MynoAno (full-stack)

Social discovery app: **MYNO** (public profile, Nearby, Events) and **ANO** (private inner circle), plus Tea, Chats, safety tools.

```
mynoano/
  backend/    Spring Boot 3 (Java 17), JPA + PostgreSQL, JWT security
  frontend/   React 18 + Vite + React Router (JSX)
  docker-compose.yml   PostgreSQL for local development
```

## Run it

1. Database: `docker compose up -d` (PostgreSQL on 5432, db/user/password: `mynoano`).
2. Backend: `cd backend && mvn spring-boot:run` (needs JDK 17+ and Maven). API on http://localhost:8080.
3. Frontend: `cd frontend && npm install && npm run dev`. App on http://localhost:5173 (Vite proxies `/api` and `/files` to the backend).

Dev sign-up: the OTP is fixed to `123456` (`OTP_DEV_CODE`) and also printed in the backend log. Use a `.edu` or `.ac.in` email to get campus mode.

Config is in `backend/src/main/resources/application.properties` and can be overridden with environment variables:
`DB_URL, DB_USER, DB_PASSWORD, JWT_SECRET (base64, 32+ bytes), CORS_ORIGIN, UPLOAD_DIR, OTP_DEV_CODE`.

## Backend layout (`com.mynoano`)

| Package | What is in it |
|---|---|
| `config` | `SecurityConfig` (stateless JWT, CORS, BCrypt), `WebConfig` (serves uploaded photos at `/files/**`) |
| `security` | `JwtService`, `JwtAuthFilter` |
| `entity` | `User, Profile, OtpCode, Friendship, Wave, Message, Event, EventParticipant, EventPhoto, EventMessage, TeaPost, TeaReaction, Invite, InviteJoin, Block, Report, LocationShare` + enums |
| `repository` | one Spring Data repository per entity |
| `dto` | `Dtos.java`: every request/response record |
| `service` | `AuthService, ProfileService, DiscoveryService, FriendService, ChatService, EventService, TeaService, InviteService, SafetyService, CleanupJob` + helpers (`Access`, `ViewFactory`, `FileStorageService`, `OtpSender`) |
| `controller` | `Auth, Profile, Discovery, Friend, Chat, Event, Tea, Safety` controllers |
| `exception` | `ApiException`, `GlobalExceptionHandler` |

## API summary (all except `/api/auth/**` need `Authorization: Bearer <jwt>`)

- Auth: `POST /api/auth/register | verify | resend | login`
- Me: `GET /api/me`, `PUT /api/me/profile | intent | discovery | location`, `POST /api/me/selfie-verify`, `GET /api/me/ano`, `GET /api/people/{id}`
- Nearby: `GET /api/nearby`; invites: `GET/POST /api/invites`, `POST /api/invites/{id}/join`, `DELETE /api/invites/{id}`
- Friends: `GET /api/friends`, `GET /api/friends/requests`, `POST /api/friends/{id}/request | accept`, `DELETE /api/friends/{id}`, `POST /api/friends/{id}/ano/request | accept`, `DELETE /api/friends/{id}/ano`, `POST /api/people/{id}/wave`, `GET /api/waves`
- Chats: `GET /api/chats`, `GET/POST /api/chats/{userId}` (messages can carry a shared event or tea post)
- Events: `GET /api/events?lat&lng&radius`, `POST /api/events`, `GET /api/events/{id}`, `POST/DELETE /api/events/{id}/join | checkin`, `POST /api/events/{id}/end | photos`, `GET/POST /api/events/{id}/chat`
- Tea: `GET /api/tea?filter=all|MYNO|ANO`, `POST /api/tea`, `POST /api/tea/{id}/react`, `DELETE /api/tea/{id}`
- Safety: `POST/DELETE /api/safety/block/{id}`, `GET /api/safety/blocked`, `POST /api/safety/report/{id}`, `POST/GET/DELETE /api/safety/share`, `PUT /api/safety/share/location`, `GET /api/safety/shared-with-me`

## Rules enforced on the server

- One account per email and per phone number; login needs a verified account.
- Nearby never returns distance or coordinates; stored positions are snapped to a ~22 m grid and expire after 10 minutes.
- Blocking is two-way and hides people everywhere (Nearby, profiles, chat, tea, events, invites).
- ANO tea, ANO events and ANO shares are visible only to the author's ANO circle (both sides must agree to join it).
- Nameless tea never exposes the author id and never shows on a profile.
- Tea (24 h), invites, location shares and ghost-mode timers are cleaned by `CleanupJob` every minute; events drop out of every list at their completion date and their chat closes.

## Before going to production

- Replace `ConsoleOtpSender` with a real SMS/email provider and set `OTP_DEV_CODE` to empty.
- `POST /api/me/selfie-verify` is a stub: connect a liveness / face-match provider first.
- Set a strong `JWT_SECRET`, serve over HTTPS, add rate limiting (login, OTP, wave, location), and use Flyway/Liquibase instead of `ddl-auto=update`.
- Add moderation tools for reports and a queue for anonymous tea.
- Nearby protection against triangulation needs rate limits and server-side jitter; the grid snapping here is a first step.
- Add push notifications (friend requests, waves, messages); chat and event chat currently poll.
- Store photos in S3 or similar instead of local disk; check image content before showing it.
- Add automated tests (none are included).
