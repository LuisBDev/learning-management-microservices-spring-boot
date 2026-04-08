# Learning Management System -- Microservices Architecture

A production-oriented Learning Management System (LMS) built as a distributed microservices ecosystem. Designed for a university/campus virtual environment similar to Moodle, with independent services for identity, courses, enrollments, and learning workflows.

## Table of Contents

- [Getting Started](#getting-started)
- [Architecture Overview](#architecture-overview)
- [Technology Stack](#technology-stack)
- [Services Inventory](#services-inventory)
- [Distributed Transactions (SAGA Pattern)](#distributed-transactions-saga-pattern)
- [Infrastructure](#infrastructure)
- [Security Model](#security-model)
- [Inter-Service Communication](#inter-service-communication)
- [Observability](#observability)
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

The system follows a microservices architecture where each service is an independent Maven project. Services communicate synchronously via REST (RestClient), discover each other through Eureka, and use **Kafka** for asynchronous compensation in distributed transactions.

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

| Component            | Technology                                       |
|:---------------------|:-------------------------------------------------|
| Language             | Java 25                                          |
| Framework            | Spring Boot 4.0.4 / Spring Framework 7           |
| Cloud                | Spring Cloud 2025.1.1 (Oakwood)                  |
| Messaging            | **Apache Kafka (KRaft mode)**                    |
| Resiliency           | **Resilience4j (Circuit Breaker, Retry)**        |

### Dependencies

| Category             | Library                         | Version  |
|:---------------------|:--------------------------------|:---------|
| Messaging            | Spring for Apache Kafka         | managed  |
| Resiliency           | Resilience4j                    | managed  |
| Service Discovery    | Spring Cloud Netflix Eureka     | managed  |
| Configuration        | Spring Cloud Config Server      | managed  |
| API Gateway          | Spring Cloud Gateway (WebFlux)  | managed  |
| Persistence          | Spring Data JPA / Hibernate     | managed  |
| Migration            | Flyway                          | managed  |
| Database             | PostgreSQL 16 (Alpine)          | managed  |
| Security             | Spring Security                 | managed  |
| JWT                  | jjwt (io.jsonwebtoken)          | 0.13.0   |
| Mapping              | MapStruct                       | 1.6.3    |
| API Docs             | Springdoc OpenAPI (Swagger UI)  | 3.0.2    |
| Metrics              | Micrometer + Prometheus         | managed  |
| Tracing              | Micrometer Tracing + OTel       | managed  |

---

## Services Inventory

### Infrastructure Services

| Service                  | Port  | Responsibility                              |
|:-------------------------|------:|:--------------------------------------------|
| `config-server-v1`       | 4488  | Centralized configuration (Git backend)     |
| `discovery-server-v1`    | 8761  | Service registry and discovery (Eureka)     |
| `api-gateway-v1`         | 8080  | Reactive API gateway, routing, load balance |
| `kafka-kraft`            | 9092  | Message broker for async communication      |
| `kafka-ui`               | 8055  | Web UI for Kafka cluster management         |

### Business Services

| Service                         | Port  | Database           | Responsibility                                  |
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

## Inter-Service Communication

### Communication Map

| Source Service     | Target Service       | Purpose                                                   |
|:-------------------|:---------------------|:----------------------------------------------------------|
| `enrollment`       | `course-service-v1`  | Verify course exists and is `PUBLISHED`                   |
| `enrollment`       | `identity-service`   | Verify user exists before enrollment                      |
| `learning`         | `enrollment-service` | Verify student has an `ACTIVE` enrollment                 |
| `orchestrator`     | `learning/enroll/course`| Coordinate SAGA steps (Create/Delete)                  |

---

## Infrastructure

### Docker Compose Services

```
docker-compose.yml
  identity-db-v1         PostgreSQL 16 Alpine  :41010
  course-db-v1           PostgreSQL 16 Alpine  :41020
  enrollment-db-v1       PostgreSQL 16 Alpine  :41030
  learning-db-v1         PostgreSQL 16 Alpine  :41040
  kafka-kraft            Confluent Kafka 7.5   :9092
  kafka-ui-kraft         Kafka UI              :8055
  prometheus-v1          Prometheus             :9090
  grafana-v1             Grafana                :3000
  jaeger-v1              Jaeger All-in-One      :16686
```

---

## API Documentation

### Orchestrator SAGA (`/api/v1/orchestrator/saga`)
- `POST /course-enrollment` -- Create course and enroll students.
- `DELETE /courses/{id}` -- Physical cascade delete of a course (SAGA).

### Enrollment Service (`/api/v1/enrollments`)
- `POST /enrollments` -- Enroll student (validates user & course).
- `DELETE /courses/{courseId}` -- Bulk delete enrollments for a course.
- `GET /{id}/history` -- Enrollment event log.

### Learning Service (`/api/v1/learning/courses`)
- `DELETE /{courseId}` -- Bulk delete all learning data (submissions, grades, files).

### Course Service (`/api/v1/courses`)
- `DELETE /{id}` -- Physical cascade delete (sections, resources, etc.).

---

## Architecture Decisions

| Decision                          | Rationale                                                                 |
|:----------------------------------|:--------------------------------------------------------------------------|
| **SAGA Orchestration**            | Centralized control for complex distributed transactions.                 |
| **Kafka for Compensation**        | Asynchronous decoupling for rollback actions.                             |
| **Hard Delete via SAGA**          | Ensures absolute consistency and data cleanup across bounded contexts.    |
| **Resilience4j Retry**            | High availability for critical physical deletion workflows.                |
| **Inter-service Validation**      | services validate IDs (User/Course) via REST before processing commands.  |
| **ProblemDetail (RFC 7807)**      | Standardized error response format across all services.                   |

---

## License

This project is for educational and portfolio purposes.
