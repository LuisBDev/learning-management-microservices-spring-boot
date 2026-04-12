# Learning Management System -- Microservices Architecture

A production-oriented Learning Management System (LMS) built as a distributed microservices ecosystem. Designed for a university/campus virtual environment similar to
Moodle, with independent services for identity, courses, enrollments, and learning workflows.

## Table of Contents

- [Getting Started](#getting-started)
- [Architecture Overview](#architecture-overview)
- [Technology Stack](#technology-stack)
- [Services Inventory](#services-inventory)
- [Distributed Transactions (SAGA Pattern)](#distributed-transactions-saga-pattern)
- [Infrastructure](#infrastructure)
- [Context Propagation & Observability](#context-propagation--observability)
- [Security Model](#security-model)
- [Inter-Service Communication](#inter-service-communication)
- [Database Strategy](#database-strategy)
- [Configuration Management](#configuration-management)
- [API Documentation](#api-documentation)
- [Architecture Decisions](#architecture-decisions)

---

## Getting Started

### Prerequisites

- Java 25+
- Maven 3.9+
- Docker and Docker Compose

### 1. Start infrastructure

```bash
docker compose up -d
```

This starts all PostgreSQL databases, Kafka (KRaft), Prometheus, Grafana, and Jaeger.

### 2. Set environment variables

```bash
export CONFIG_REPO_TOKEN=<your-github-pat>
```

### 3. Start services in order

```bash
# 1. Config Server (must start first)
cd config-server-v1 && mvn spring-boot:run

# 2. Discovery Server
cd discovery-server-v1 && mvn spring-boot:run

# 3. API Gateway
cd api-gateway-v1 && mvn spring-boot:run

# 4. Business services (any order)
cd identity-service-v1 && mvn spring-boot:run
cd course-service-v1 && mvn spring-boot:run
cd enrollment-service-v1 && mvn spring-boot:run
cd learning-service-v1 && mvn spring-boot:run
cd admin-orchestrator-service-v1 && mvn spring-boot:run
```

### 4. Verify

- Eureka Dashboard: http://localhost:8761
- Kafka UI: http://localhost:8055
- Swagger UI (example): http://localhost:40020/swagger-ui.html
- Prometheus: http://localhost:9090/targets
- Jaeger: http://localhost:16686

---

## Architecture Overview

The system follows a microservices architecture where each service is an independent Maven project. Services communicate synchronously via REST (RestClient), discover
each other through Eureka, and use **Kafka** for asynchronous compensation in distributed transactions.

```
                        +-------------------+
                        |   API Gateway     |
                        |   (WebFlux)       |
                        |   :8080           |
                        +---------+---------+
                                  |
              +-------------------+-------------------+
              |                   |                   |
    +---------v------+  +---------v------+  +---------v------+
    | Identity       |  | Course         |  | Enrollment     |
    | Service        |  | Service        |  | Service        |
    | :40010         |  | :40020         |  | :40030         |
    +--------+-------+  +--------+-------+  +--------+-------+
             |                   |                   |
    +--------v-------+  +--------v-------+  +--------v-------+
    | identity-db    |  | course-db      |  | enrollment-db  |
    | :41010         |  | :41020         |  | :41030         |
    +----------------+  +----------------+  +----------------+

              +-------------------+
              | Learning Service  |
              | :40040            |
              +--------+----------+
                       |
              +--------v----------+
              | learning-db       |
              | :41040            |
              +-------------------+

    +----------------+  +----------------+  +----------------+
    | Config Server  |  | Discovery      |  | Admin Orch.    |
    | :4488          |  | (Eureka)       |  | :40050         |
    |                |  | :8761          |  | (SAGA)         |
    +----------------+  +----------------+  +----------------+
                                |
                        +-------v-------+
                        |     Kafka     |
                        |    (KRaft)    |
                        +---------------+
```

---

## Technology Stack

### Core

| Component  | Technology                                |
|:-----------|:------------------------------------------|
| Language   | Java 25                                   |
| Framework  | Spring Boot 4.0.4 / Spring Framework 7    |
| Cloud      | Spring Cloud 2025.1.1 (Oakwood)           |
| Messaging  | **Apache Kafka (KRaft mode)**             |
| Resiliency | **Resilience4j (Circuit Breaker, Retry)** |

### Dependencies

| Category          | Library                        | Version |
|:------------------|:-------------------------------|:--------|
| Messaging         | Spring for Apache Kafka        | managed |
| Resiliency        | Resilience4j                   | managed |
| Service Discovery | Spring Cloud Netflix Eureka    | managed |
| Configuration     | Spring Cloud Config Server     | managed |
| API Gateway       | Spring Cloud Gateway (WebFlux) | managed |
| Persistence       | Spring Data JPA / Hibernate    | managed |
| Migration         | Flyway                         | managed |
| Database          | PostgreSQL 16 (Alpine)         | managed |
| Security          | Spring Security                | managed |
| JWT               | jjwt (io.jsonwebtoken)         | 0.13.0  |
| Mapping           | MapStruct                      | 1.6.3   |
| API Docs          | Springdoc OpenAPI (Swagger UI) | 3.0.2   |
| Metrics           | Micrometer + Prometheus        | managed |
| Tracing           | Micrometer Tracing + OTel      | managed |

---

## Services Inventory

### Infrastructure Services

| Service               | Port | Responsibility                              |
|:----------------------|-----:|:--------------------------------------------|
| `config-server-v1`    | 4488 | Centralized configuration (Git backend)     |
| `discovery-server-v1` | 8761 | Service registry and discovery (Eureka)     |
| `api-gateway-v1`      | 8080 | Reactive API gateway, routing, load balance |
| `kafka-kraft`         | 9092 | Message broker for async communication      |
| `kafka-ui`            | 8055 | Web UI for Kafka cluster management         |

### Business Services

| Service                         |  Port | Database           | Responsibility                                  |
|:--------------------------------|------:|:-------------------|:------------------------------------------------|
| `identity-service-v1`           | 40010 | `identity-db-v1`   | Authentication, users, roles, permissions, JWT  |
| `course-service-v1`             | 40020 | `course-db-v1`     | Courses, sections, resources, assignments       |
| `enrollment-service-v1`         | 40030 | `enrollment-db-v1` | Enrollments, status transitions, event history  |
| `learning-service-v1`           | 40040 | `learning-db-v1`   | Submissions, submission files, grading, cleanup |
| `admin-orchestrator-service-v1` | 40050 | none               | SAGA orchestration (Create/Delete Course)       |

---

## Distributed Transactions (SAGA Pattern)

The `admin-orchestrator-service-v1` implements the **SAGA Pattern (Orchestration-based)** to coordinate complex transactions.

### 1. Create Course with Enrollment (Mixed)

Ensures a course is created and students are enrolled atomically.

- **Compensation:** If enrollment fails, the orchestrator triggers a **Kafka** event (`course-compensation-events`) to delete the course in `course-service-v1`.

### 2. Physical Course Deletion (Linear)

Deletes a course and all related data across 3 microservices.

- **Step 1:** `learning-service-v1` -> Delete submissions, grades, and files.
- **Step 2:** `enrollment-service-v1` -> Delete enrollments and event history.
- **Step 3:** `course-service-v1` -> Delete sections, resources, teachers, and course.
- **Resiliency:** Uses **Resilience4j @Retry** to handle transient failures, ensuring physical cleanup is completed.

---

## Context Propagation & Observability

### Distributed Tracing (OTLP)

- **Library:** Micrometer Tracing with OpenTelemetry (OTel).
- **Exporting:** Traces are sent via **OTLP HTTP** to Jaeger (`http://localhost:4318/v1/traces`).
- **Propagation:** Trace IDs are automatically propagated across microservices using W3C headers. This allows end-to-end transaction visibility in Jaeger.

### Metrics & Dashboards

- **Prometheus:** Services expose metrics at `/actuator/prometheus`.
- **Grafana:** Dashboards visualize performance, error rates, and Kafka metrics.

### Context Propagation

The system ensures that both security and operational context are preserved across inter-service calls:

| Context Type         | Mechanism                        | Purpose                                   |
|:---------------------|:---------------------------------|:------------------------------------------|
| **Security (JWT)**   | `AuthorizationHeaderInterceptor` | Propagates User identity and permissions. |
| **Tracing (B3/W3C)** | `Micrometer Tracing`             | Links spans across microservices.         |
| **Logging**          | `Slf4j MDC`                      | Injects `traceId` into every log entry.   |

---

## Inter-Service Communication

### Communication Map

| Source Service | Target Service           | Purpose                                   |
|:---------------|:-------------------------|:------------------------------------------|
| `enrollment`   | `course-service-v1`      | Verify course exists and is `PUBLISHED`   |
| `enrollment`   | `identity-service`       | Verify user exists before enrollment      |
| `learning`     | `enrollment-service`     | Verify student has an `ACTIVE` enrollment |
| `orchestrator` | `learning/enroll/course` | Coordinate SAGA steps (Create/Delete)     |

---

## Database Strategy

**Database per service.** Each business microservice owns its database exclusively. There are no cross-database foreign keys. Integration between bounded contexts is
resolved through REST API calls.

---

## API Documentation

### Swagger UI by service

| Service                         | Swagger URL                              |
|:--------------------------------|:-----------------------------------------|
| `identity-service-v1`           | `http://localhost:40010/swagger-ui.html` |
| `course-service-v1`             | `http://localhost:40020/swagger-ui.html` |
| `enrollment-service-v1`         | `http://localhost:40030/swagger-ui.html` |
| `learning-service-v1`           | `http://localhost:40040/swagger-ui.html` |
| `admin-orchestrator-service-v1` | `http://localhost:40050/swagger-ui.html` |

### Authentication quick flow

Most business endpoints require a JWT Bearer token.

1) Register user

```bash
curl -X POST http://localhost:40010/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@gmail.com",
    "firstName": "admin",
    "lastName": "Balarezo",
    "password": "luis123456",
    "role": "ROLE_ADMIN"
  }'
```

2) Login

```bash
curl -X POST http://localhost:40010/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@gmail.com",
    "password": "luis123456"
  }'
```

Example response (shortened):

```json
{
  "access_token": "eyJhbGciOiJI...",
  "refresh_token": "eyJhbGciOiJI...",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

---

### Admin Orchestrator SAGA (`/api/v1/orchestrator/saga`)

#### `POST /course-enrollment`

Creates a course and triggers enrollment for a list of students (orchestration flow).

- Auth: ROLE_ADMIN in JWT
- Success: `200 OK` with message
- Failure: `4xx/5xx` with `ProblemDetail`

```bash
curl -X POST http://localhost:40050/api/v1/orchestrator/saga/course-enrollment \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "CV-PBI-2026-1",
    "courseName": "Power BI 01",
    "courseSummary": "Intro course for BI fundamentals",
    "studentIds": [
      "a0089e7d-9589-4496-9be0-6745a04ae3ad",
      "e3559dd6-b872-470f-94e8-4a524f5e3a3f"
    ]
  }'
```

Success response:

```text
SAGA initiated successfully
```

#### `DELETE /courses/{id}`

Runs physical cascade deletion across learning, enrollment, and course services.

- Auth: admin JWT
- Success: `204 No Content`

```bash
curl -X DELETE http://localhost:40050/api/v1/orchestrator/saga/courses/97121825-56e2-4ada-b150-39a07e85d3ff \
  -H "Authorization: Bearer <access_token>"
```

---

### Course Service (`/api/v1/courses`)

#### `GET /api/v1/courses?page=0&size=20`

Returns paginated courses.

```bash
curl -X GET "http://localhost:40020/api/v1/courses?page=0&size=20" \
  -H "Authorization: Bearer <access_token>"
```

Example response:

```json
{
  "content": [
    {
      "id": "97121825-56e2-4ada-b150-39a07e85d3ff",
      "code": "CV-PBI-2026-1",
      "title": "Power BI 01",
      "summary": "Resumen",
      "status": "PUBLISHED",
      "createdBy": "a0089e7d-9589-4496-9be0-6745a04ae3ad",
      "createdAt": "2026-04-06T18:29:54.433642Z",
      "updatedAt": "2026-04-06T18:29:54.433642Z"
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

#### `PATCH /api/v1/courses/{id}/status`

Updates publication status.

```bash
curl -X PATCH http://localhost:40020/api/v1/courses/97121825-56e2-4ada-b150-39a07e85d3ff/status \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{"status":"PUBLISHED"}'
```

---

### Enrollment Service (`/api/v1/enrollments`)

#### `POST /api/v1/enrollments`

Enrolls one student in one course (validates user and course state).

```bash
curl -X POST http://localhost:40030/api/v1/enrollments \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "courseId": "97121825-56e2-4ada-b150-39a07e85d3ff",
    "studentUserId": "a0089e7d-9589-4496-9be0-6745a04ae3ad"
  }'
```

#### `DELETE /api/v1/enrollments/courses/{courseId}`

Bulk deletion endpoint used by the orchestrator during SAGA compensation/cleanup.

---

### Learning Service (`/api/v1/learning/courses`)

#### `DELETE /api/v1/learning/courses/{courseId}`

Removes submissions, grades, and files linked to a course.

---

### Standard error format (`ProblemDetail`)

Most services return RFC 7807 style errors.

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Course not found: 97121825-56e2-4ada-b150-39a07e85d3ff",
  "instance": "/api/v1/courses/97121825-56e2-4ada-b150-39a07e85d3ff"
}
```

---

## Architecture Decisions

| Decision                     | Rationale                                                              |
|:-----------------------------|:-----------------------------------------------------------------------|
| **SAGA Orchestration**       | Centralized control for complex distributed transactions.              |
| **Kafka for Compensation**   | Asynchronous decoupling for rollback actions.                          |
| **Hard Delete via SAGA**     | Ensures absolute consistency and data cleanup across bounded contexts. |
| **Resilience4j Retry**       | High availability for critical physical deletion workflows.            |
| **ProblemDetail (RFC 7807)** | Standardized error response format across all services.                |

---

## License

This project is for educational and portfolio purposes.
