# Refyn — Microservices Job Portal Platform (Backend)

A distributed job portal backend built entirely around microservices architecture, event-driven communication, and fault tolerance. This is a **backend-focused practice project** — there is intentionally no frontend. The goal was to design and implement a production-style distributed system, not a UI.

## Why No Frontend

Refyn was built to practice distributed systems patterns: service decomposition, async messaging, resilience, and centralized configuration. All of that lives in the backend. The API layer is fully testable and explorable via the Postman collection below — a UI wouldn't add anything to what this project is demonstrating.

## Architecture

Five independently deployable microservices, each owning its own domain:

```
                         ┌─────────────────┐
                         │  Kong API Gateway │
                         └────────┬─────────┘
                                  │
        ┌──────────┬─────────────┼─────────────┬──────────────┐
        ▼          ▼             ▼             ▼              ▼
   Auth Service  User Service  Job Service  Application    Notification
                                              Service        Service
        │          │             │             │              │
        └──────────┴─────────────┴─────────────┴──────────────┘
                           Kafka Event Bus
                  (async, decoupled inter-service comms)

              Spring Cloud Config ──► centralized config for all services
              Resilience4j ──► circuit breaking + fallbacks per service
```

- **Auth Service** — authentication and JWT issuance
- **User Service** — user profile and account management
- **Job Service** — job postings, search, and listings
- **Application Service** — job applications and status tracking
- **Notification Service** — consumes Kafka events and delivers notifications

## Key Design Decisions

- **Kafka over direct REST calls** — services publish events (e.g., "application submitted") instead of calling each other synchronously. This decouples services and means a slow/down Notification Service doesn't block job applications.
- **Resilience4j circuit breakers** — prevent cascading failures when a downstream service is degraded; fallback responses keep the system usable.
- **Spring Cloud Config** — one source of truth for configuration across all five services instead of duplicated `application.yml` files.
- **Kong API Gateway** — single entry point for routing, rate limiting, and auth delegation to the Auth Service.
- **Docker Compose** — spins up all five services, Kafka, Kong, and dependencies with one command for local development.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Framework | Spring Boot |
| Messaging | Apache Kafka |
| Resilience | Resilience4j |
| Config | Spring Cloud Config |
| Gateway | Kong |
| Auth | JWT |
| Containerization | Docker, Docker Compose |

## Getting Started

```bash
# Clone the repo
git clone https://github.com/anmolvats001/Refyn.git
cd refyn

# Start all services, Kafka, and Kong via Docker Compose
docker-compose up --build

# Services will be available behind Kong at http://localhost:<gateway-port>
```

## What This Project Demonstrates

- Designing service boundaries around business capabilities rather than technical layers
- Event-driven communication and eventual consistency trade-offs
- Fault isolation — one service failing doesn't take down the whole platform
- Centralized configuration management at scale

## Possible Next Steps

- Kubernetes manifests for orchestrated deployment
- Observability stack (Prometheus + Grafana) for metrics across services
- Integration tests with Testcontainers spanning service boundaries

## License

MIT
