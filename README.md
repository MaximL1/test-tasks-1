# 🛒 Order Management API

This is a Spring Boot-based REST API for managing orders, featuring:

- 🔐 Spring Security with OAuth2 Resource Server
- 🧩 Resilience4j for fault tolerance and rate limiting
- 🧬 MapStruct for DTO ↔ Entity mapping
- 🗃️ H2 In-Memory Database
- 🧼 Bean Validation (JSR-380)
- 🐳 Docker & Docker Compose support
- 📖 OpenAPI (Swagger UI)

---

## 🚀 Features

- Create, update, and search orders
- Input validation with automatic error responses
- Rate-limiting using Resilience4j
- OAuth2 JWT token-based authentication
- API documentation via Swagger UI
- Containerized and ready for deployment

---

## 📦 Tech Stack

| Tech                  | Version        |
|-----------------------|----------------|
| Java                  | 17             |
| Spring Boot           | 3.4.5-SNAPSHOT |
| Spring Data JPA       | ✅             |
| Spring Security (OAuth2) | ✅          |
| Resilience4j          | 2.3.0          |
| MapStruct             | 1.6.3          |
| H2 Database           | ✅             |
| Docker                | ✅             |

---

## ⚙️ Getting Started

### 🔧 Build the App

```bash
./gradlew clean build

🐳 Run with Docker Compose
docker-compose up --build

🔐 Security
Authorization: Bearer <your-token>

📊 API Documentation
http://localhost:8080/api/v1/swagger-ui/index.html#/

💾 H2 Console (for dev)
http://localhost:8080/h2-console

Use JDBC URL:
jdbc:h2:mem:testdb

🧪 Run Tests
./gradlew test
