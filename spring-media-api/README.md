# Spring Media API (Java + Spring Boot)

Implements:
- DB Schemas: `MediaAsset`, `AdminUser`, `MediaViewLog`
- Auth: `POST /auth/signup`, `POST /auth/login` (returns JWT)
- Media: `POST /media` (authenticated)
- Stream URL: `GET /media/{id}/stream-url` (authenticated) → generates a secure 10-min link
- Stream: `GET /stream?token=...` (public) → validates token, logs view, redirects (302) to file_url

## Quick Start
```bash
# Java 17+ required
mvn spring-boot:run
```

### Default config
- In-memory H2 DB
- H2 console at `/h2-console`
- Change secrets in `src/main/resources/application.properties`.

## API Examples

### 1) Signup
```http
POST /auth/signup
Content-Type: application/json

{ "email": "admin@example.com", "password": "pass1234" }
```

### 2) Login
```http
POST /auth/login
Content-Type: application/json

{ "email": "admin@example.com", "password": "pass1234" }
```
Response:
```json
{ "token": "eyJhbGciOi..." }
```

### 3) Create Media (Authenticated)
```http
POST /media
Authorization: Bearer <token>
Content-Type: application/json

{ "title": "Sample Video", "type": "VIDEO", "fileUrl": "https://example.com/video.mp4" }
```

### 4) Get 10-min Stream URL (Authenticated)
```http
GET /media/1/stream-url
Authorization: Bearer <token>
```
Response:
```json
{
  "url": "http://localhost:8080/stream?token=...",
  "expiresAtEpochMs": 1710000000000
}
```

### 5) Use Stream URL (No auth)
```http
GET /stream?token=...
```
→ 302 redirect to the actual `fileUrl`, and a view log is created.

## Switch to MySQL
Change `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/media_db
spring.datasource.username=root
spring.datasource.password=yourpass
spring.jpa.hibernate.ddl-auto=update
```

## Notes
- JWT for auth and separate JWT for stream links (10-min expiry).
- Stream token optionally bound to IP address.
- Replace `fileUrl` with your CDN/S3 URL. You can also remove IP lock by editing `StreamController`.
