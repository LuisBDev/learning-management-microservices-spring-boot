# Learning Management System -- Microservices Architecture

A production-oriented Learning Management System (LMS) built as a distributed microservices ecosystem. Designed for a university/campus virtual environment similar to Moodle, with independent services for identity, courses, enrollments, and learning workflows.

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Technology Stack](#technology-stack)
- [Services Inventory](#services-inventory)
- [Project Structure](#project-structure)
- [Infrastructure](#infrastructure)
- [Security Model](#security-model)
- [Inter-Service Communication](#inter-service-communication)
- [Observability](#observability)
- [Database Strategy](#database-strategy)
- [Configuration Management](#configuration-management)
- [API Documentation](#api-documentation)
- [Getting Started](#getting-started)
- [Architecture Decisions](#architecture-decisions)

---

## Architecture Overview

The system follows a microservices architecture where each service is an independent Maven project (not a multi-module build). Services communicate synchronously via REST, discover each other through Eureka, and share configuration through a centralized Config Server backed by Git.

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
              | Learning Service  |-----> enrollment-service (RestClient)
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
    |                |  | :8761          |  | (placeholder)  |
    +----------------+  +----------------+  +----------------+
```

---

## Technology Stack

### Core

| Component            | Technology                                       |
|:---------------------|:-------------------------------------------------|
| Language             | Java 25                                          |
| Framework            | Spring Boot 4.0.4 / Spring Framework 7           |
| Specification        | Jakarta EE 11                                    |
| Cloud                | Spring Cloud 2025.1.1 (Oakwood)                  |
| Build Tool           | Apache Maven                                     |

### Dependencies

| Category             | Library                         | Version  |
|:---------------------|:--------------------------------|:---------|
| Service Discovery    | Spring Cloud Netflix Eureka     | managed  |
| Configuration        | Spring Cloud Config Server      | managed  |
| API Gateway          | Spring Cloud Gateway (WebFlux)  | managed  |
| Persistence          | Spring Data JPA / Hibernate     | managed  |
| Migration            | Flyway                          | managed  |
| Database             | PostgreSQL 16 (Alpine)          | managed  |
| Security             | Spring Security                 | managed  |
| JWT                  | jjwt (io.jsonwebtoken)          | 0.13.0   |
| Mapping              | MapStruct                       | 1.6.3    |
| Boilerplate          | Lombok                          | managed  |
| Lombok-MapStruct     | lombok-mapstruct-binding        | 0.2.0    |
| API Docs             | Springdoc OpenAPI (Swagger UI)  | 3.0.2    |
| Metrics              | Micrometer + Prometheus         | managed  |
| Tracing              | Micrometer Tracing + OTel       | managed  |
| Tracing Exporter     | OpenTelemetry OTLP Exporter     | managed  |

---

## Services Inventory

### Infrastructure Services

| Service                  | Port  | Responsibility                              |
|:-------------------------|------:|:--------------------------------------------|
| `config-server-v1`       | 4488  | Centralized configuration (Git backend)     |
| `discovery-server-v1`    | 8761  | Service registry and discovery (Eureka)     |
| `api-gateway-v1`         | 8080  | Reactive API gateway, routing, load balance |

### Business Services

| Service                         | Port  | Database           | Responsibility                                  |
|:--------------------------------|------:|:-------------------|:------------------------------------------------|
| `identity-service-v1`           | 40010 | `identity-db-v1`   | Authentication, users, roles, permissions, JWT  |
| `course-service-v1`             | 40020 | `course-db-v1`     | Courses, sections, resources, assignments       |
| `enrollment-service-v1`         | 40030 | `enrollment-db-v1` | Enrollments, status transitions, event history  |
| `learning-service-v1`           | 40040 | `learning-db-v1`   | Submissions, submission files, grading          |
| `admin-orchestrator-service-v1` | 40050 | none               | Future orchestration layer (placeholder)        |

---

## Project Structure

Each business microservice follows a layered architecture inspired by Clean Architecture and lightweight DDD:

```
service-name-v1/
  src/main/java/com/lms/{domain}/
    application/
      service/              Interface + implementation for use cases
    config/                 Spring beans, security, JPA auditing
    controller/
      dto/
        request/            Inbound DTOs with validation annotations
        response/           Outbound DTOs
      mapper/               MapStruct interfaces (Entity <-> Response)
      *Controller.java      REST controllers (no business logic)
    domain/
      model/
        enums/              Domain enumerations
    exception/              Custom exceptions + GlobalExceptionHandler
    infrastructure/
      client/               RestClient-based inter-service clients
      persistence/
        entity/             JPA entities with Lombok annotations
        repository/         Spring Data JPA repositories
    security/               JWT validation, filters, security helpers
  src/main/resources/
    application.yml         Local config (name, profile)
    application-dev.yml     Config Server import
    db/migration/           Flyway SQL scripts
```

### Layer Responsibilities

| Layer              | Knows About           | Responsibility                                        |
|:-------------------|:----------------------|:------------------------------------------------------|
| `controller`       | Request/Response DTOs | HTTP binding, validation, delegation to service        |
| `application`      | Domain + interfaces   | Business orchestration, `@Transactional` boundaries    |
| `domain`           | Nothing external      | Enums, domain rules                                    |
| `infrastructure`   | JPA, RestClient       | Persistence adapters, external service clients         |
| `security`         | JWT, Spring Security  | Token validation, authentication filter, authorization |

---

## Infrastructure

All infrastructure runs in Docker containers. Business services run on the host JVM.

### Docker Compose Services

```
docker-compose.yml
  identity-db-v1         PostgreSQL 16 Alpine  :41010 -> 5432
  course-db-v1           PostgreSQL 16 Alpine  :41020 -> 5432
  enrollment-db-v1       PostgreSQL 16 Alpine  :41030 -> 5432
  learning-db-v1         PostgreSQL 16 Alpine  :41040 -> 5432
  prometheus-v1          Prometheus             :9090
  grafana-v1             Grafana                :3000
  jaeger-v1              Jaeger All-in-One      :16686 (UI), :4317 (gRPC), :4318 (HTTP)
```

### Starting Infrastructure

```bash
docker compose up -d
```

---

## Security Model

### Authentication Flow

1. Client authenticates via `POST /api/v1/auth/login` on `identity-service-v1`.
2. Identity service returns a JWT access token and a refresh token.
3. Access token includes claims: `user_id`, `roles`, `permissions`, `token_type`.
4. Client sends the token as `Authorization: Bearer <token>` in subsequent requests.
5. API Gateway forwards the header transparently to downstream services.
6. Each downstream service independently validates the JWT (defense in depth).

### JWT Validation in Consumer Services

Every business service (`course`, `enrollment`, `learning`) contains its own lightweight security stack:

- `JwtTokenProvider` -- parses and validates the token, extracts claims.
- `JwtAuthenticationFilter` -- servlet filter that populates `SecurityContextHolder`.
- `JwtAuthenticatedUser` -- principal POJO holding `userId`, `email`, and `authorities`.
- `SecurityContextHelper` -- utility to retrieve the current user from the security context.
- `JwtAuthenticationEntryPoint` / `AccessDeniedHandlerImpl` -- return RFC 7807 `ProblemDetail` responses.

The JWT signing key is centralized in Config Server (`security.jwt.secret-key`) and shared across all services.

### Authorization (RBAC)

Role-Based Access Control with fine-grained permissions. Roles and permissions are stored as database records in `identity-service-v1`, not as enums.

**Roles:** `ROLE_ADMIN`, `ROLE_TEACHER`, `ROLE_STUDENT`

`ROLE_ADMIN` holds all permissions (god role). Method-level security is enforced via `@PreAuthorize`:

| Permission           | Scope                                                     |
|:---------------------|:----------------------------------------------------------|
| `COURSE_CREATE`      | Create courses                                            |
| `COURSE_UPDATE`      | Update course metadata                                    |
| `COURSE_PUBLISH`     | Change course status (hidden/published/archived)          |
| `SECTION_MANAGE`     | Create, update, delete, reorder sections                  |
| `RESOURCE_MANAGE`    | Create, update, delete, reorder resources and recordings  |
| `ASSIGNMENT_MANAGE`  | Create and update assignments                             |
| `ASSIGNMENT_GRADE`   | Grade and update grades for submissions                   |
| `ENROLLMENT_MANAGE`  | Enroll students and change enrollment status              |
| `USER_MANAGE`        | Manage user accounts                                      |
| `ROLE_MANAGE`        | Manage roles                                              |
| `PERMISSION_MANAGE`  | Manage permissions                                        |

Read operations are accessible to any authenticated user. Delete operations on courses require `ROLE_ADMIN`. Teacher assignment to courses requires `ROLE_ADMIN`.

### Token Blacklist

`identity-service-v1` maintains a `token_blacklist` table for JWT revocation (logout). Tokens are identified by their `jti` claim.

---

## Inter-Service Communication

Synchronous HTTP communication using Spring `RestClient` with `@LoadBalanced` for Eureka-based service discovery. An interceptor automatically propagates the `Authorization` header from the incoming request to outgoing inter-service calls.

### Communication Map

| Source Service     | Target Service       | Purpose                                                   |
|:-------------------|:---------------------|:----------------------------------------------------------|
| `enrollment`       | `course-service-v1`  | Verify course exists and is `PUBLISHED` before enrollment |
| `learning`         | `enrollment-service` | Verify student has an `ACTIVE` enrollment before submission |

### RestClient Configuration

Each consuming service defines a `RestClientConfig` bean:
- `@LoadBalanced RestClient.Builder` for Eureka resolution.
- Request interceptor that forwards the JWT `Authorization` header via `RequestContextHolder`.

---

## Observability

### Metrics

All services expose Prometheus metrics at `/actuator/prometheus`. Prometheus scrapes every 15 seconds.

### Distributed Tracing

OpenTelemetry traces are exported via OTLP HTTP to Jaeger at `http://localhost:4318/v1/traces`. Sampling probability is set to `1.0` for development.

### Health Checks

Spring Boot Actuator is enabled on all services with health detail visibility (`show-details: always`).

### Dashboards

| Tool       | URL                          | Purpose                      |
|:-----------|:-----------------------------|:-----------------------------|
| Prometheus | http://localhost:9090        | Metrics queries              |
| Grafana    | http://localhost:3000        | Metrics visualization        |
| Jaeger     | http://localhost:16686       | Distributed trace explorer   |
| Swagger UI | http://localhost:{port}/swagger-ui.html | Per-service API docs |

---

## Database Strategy

**Database per service.** Each business microservice owns its database exclusively. There are no cross-database foreign keys. Integration between bounded contexts is resolved through REST API calls.

### Schema Management

Flyway manages all schema migrations. Hibernate DDL mode is set to `validate` to ensure the schema matches the entity model without automatic modification.

Migration files are located at `src/main/resources/db/migration/` within each service.

| Service        | Migrations                                              |
|:---------------|:--------------------------------------------------------|
| `identity`     | `V1__create_identity_tables.sql`, `V2__seed_roles_permissions.sql` |
| `course`       | `V1__create_course_tables.sql`                          |
| `enrollment`   | `V1__create_enrollment_tables.sql`                      |
| `learning`     | `V1__create_learning_tables.sql`                        |

### Data Model Summary

**identity-service-v1** (6 tables): `users`, `roles`, `permissions`, `user_roles`, `role_permissions`, `token_blacklist`

**course-service-v1** (10 tables): `courses`, `course_teachers`, `course_sections`, `course_resources`, `resource_files`, `resource_texts`, `resource_urls`, `assignments`, `assignment_material_files`, `course_recordings`

**enrollment-service-v1** (2 tables): `course_enrollments`, `enrollment_events`

**learning-service-v1** (3 tables): `assignment_submissions`, `assignment_submission_files`, `assignment_grades`

### JPA Conventions

- Unidirectional `@ManyToOne` / `@OneToOne` with `FetchType.LAZY` for intra-service relationships.
- Raw UUIDs for cross-service references (no JPA relationships across bounded contexts).
- `@Enumerated(EnumType.STRING)` in Java; `varchar` columns in the database (no database-level enums).
- JPA Auditing via `@CreatedDate` / `@LastModifiedDate` on a shared `BaseAuditEntity` mapped superclass.

---

## Configuration Management

### Config Server

Spring Cloud Config Server with a Git backend pointing to this same repository. Configuration files are stored in `infra/config-server-properties/`.

```
infra/config-server-properties/
  application.yml                     Shared properties (Eureka, Actuator, tracing, JWT secret)
  {service-name}.yml                  Service-specific base properties
  {service-name}-dev.yml              Service-specific dev profile overrides
```

### Spring Profiles

| Profile         | Usage                                  |
|:----------------|:---------------------------------------|
| `dev`           | Active profile for all business services during development |
| `config-server` | Active profile for Config Server only  |

Each service imports Config Server in its `application-dev.yml`:

```yaml
spring:
  config:
    import: "optional:configserver:http://localhost:4488"
```

### Environment Variables

| Variable              | Required By        | Purpose                                    |
|:----------------------|:-------------------|:-------------------------------------------|
| `CONFIG_REPO_TOKEN`   | `config-server-v1` | GitHub PAT for Git backend authentication  |
| `CONFIG_REPO_USERNAME`| `config-server-v1` | Git username (default: `git`)              |
| `JWT_SECRET`          | All services       | JWT signing key (has dev default)          |

---

## API Documentation

Swagger UI is available on every business service at `/swagger-ui.html`. API docs are served at `/v3/api-docs`. No annotation-level documentation on controllers -- Springdoc auto-generates from the endpoint signatures.

### Key Endpoints

**Identity Service** (`/api/v1/auth`, `/api/v1/users`)
- `POST /auth/login` -- Authenticate and receive JWT tokens
- `POST /auth/register` -- Register a new user
- `POST /auth/refresh` -- Refresh an access token
- `POST /auth/logout` -- Blacklist a token

**Course Service** (`/api/v1/courses`)
- `GET /courses` -- List courses (paginated)
- `POST /courses` -- Create course (`COURSE_CREATE`)
- `PUT /courses/{id}` -- Update course (`COURSE_UPDATE`)
- `PATCH /courses/{id}/status` -- Change status (`COURSE_PUBLISH`)
- `DELETE /courses/{id}` -- Delete course (`ROLE_ADMIN`)
- Nested: `/courses/{id}/sections`, `/courses/{id}/teachers`, resources, recordings

**Enrollment Service** (`/api/v1/enrollments`)
- `POST /enrollments` -- Enroll student (`ENROLLMENT_MANAGE`)
- `PATCH /enrollments/{id}/status` -- Suspend/cancel/restore (`ENROLLMENT_MANAGE`)
- `GET /enrollments/course/{id}` -- By course (paginated)
- `GET /enrollments/student/{id}` -- By student (paginated)
- `GET /enrollments/check?courseId=&studentUserId=` -- Check specific enrollment
- `GET /enrollments/{id}/history` -- Enrollment event log

**Learning Service** (`/api/v1/submissions`, `/api/v1/grades`)
- `POST /submissions` -- Submit assignment (validates active enrollment)
- `GET /submissions/assignment/{id}` -- By assignment (paginated)
- `POST /grades` -- Grade a submission (`ASSIGNMENT_GRADE`)
- `PUT /grades/{id}` -- Update grade (`ASSIGNMENT_GRADE`)

### Pagination

List endpoints return Spring Data `Page<T>` responses. Query parameters:

```
?page=0&size=20&sort=createdAt,desc
```

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

This starts all PostgreSQL databases, Prometheus, Grafana, and Jaeger.

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
```

### 4. Verify

- Eureka Dashboard: http://localhost:8761
- Swagger UI (example): http://localhost:40020/swagger-ui.html
- Prometheus: http://localhost:9090/targets
- Jaeger: http://localhost:16686

---

## Architecture Decisions

| Decision                          | Rationale                                                                 |
|:----------------------------------|:--------------------------------------------------------------------------|
| Independent Maven projects        | Each service is independently deployable, versioned, and buildable        |
| No shared libraries               | Avoids coupling; each service owns its full codebase                      |
| Database per service              | Strong bounded context isolation; no cross-database foreign keys          |
| JWT validation in every service   | Defense in depth; services remain secure even if the gateway is bypassed  |
| Centralized JWT secret            | Single source of truth via Config Server; all services validate equally   |
| RestClient with `@LoadBalanced`   | Synchronous HTTP with Eureka resolution; automatic JWT header propagation |
| Flyway with `ddl-auto: validate`  | Explicit, versioned schema migrations; Hibernate validates at startup     |
| Unidirectional JPA relationships  | Prevents N+1 problems, avoids circular serialization, explicit queries    |
| `FetchType.LAZY` everywhere       | Performance by default; eager fetching is opt-in via queries              |
| Roles as database records         | Flexible RBAC; new roles/permissions without code changes                 |
| `ProblemDetail` (RFC 7807)        | Standardized error response format across all services                    |
| Reactive API Gateway              | Non-blocking routing layer; does not validate JWT (delegates downstream)  |
| Spring Profiles (`dev`)           | Environment-specific config without code changes                          |

---

## License

This project is for educational and portfolio purposes.
