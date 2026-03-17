# NTA CCMS Backend (DIGIT-Integrated)

This backend is structured as DIGIT-style logical services that can run independently on different ports.

## Services

- `auth-access-service` (default `8081`)
- `master-data-service` (default `8082`)
- `ticket-service` (default `8087`)
- `workflow-service` (default `8085`)

## Design Notes

- Services are independently deployable.
- API paths are versioned (`/api/v1/...`).
- Business-facing term is `Candidate`.
- Modules can run direct (local multi-port) or behind an API gateway.
- `ticket-service` is PostgreSQL + Flyway backed.
- Auth, MDMS, workflow, and notifications are integrated through DIGIT endpoints.

## Ticket Service Database

Environment variables:

- `CCMS_TICKET_DB_URL` (default `jdbc:postgresql://localhost:5432/nta_ccms`; DIGIT local usually `localhost:5434`)
- `CCMS_TICKET_DB_USERNAME` (default `postgres`)
- `CCMS_TICKET_DB_PASSWORD` (default `postgres`; DIGIT local usually `password`)

Flyway migrations are in:

- `backend/ticket-service/src/main/resources/db/migration`

## Ticket APIs (Phase-2 Baseline)

- `GET /api/v1/tickets`
- `POST /api/v1/tickets`
- `POST /api/v1/tickets/{grievanceId}/assign`
- `POST /api/v1/tickets/{grievanceId}/transition`
- `GET /api/v1/tickets/{grievanceId}/history`
- `GET /api/v1/tickets/officer-queue`

## DIGIT Service Usage

- `auth-access-service`: proxies password login and userinfo to DIGIT auth/Keycloak.
- `master-data-service`: reads exams/categories from DIGIT MDMS.
- `ticket-service`: initializes and transitions workflow via DIGIT workflow engine.
- `ticket-service`: optional DIGIT notification trigger on create/transition.

For DIGIT local bootstrap (workflow process + MDMS seeds), run:

```bash
powershell -ExecutionPolicy Bypass -File apps/nta-ccms/scripts/bootstrap-digit-local.ps1
```

## Build

```bash
mvn -f apps/nta-ccms/backend/pom.xml clean install
```

## Run (example)

```bash
mvn -f apps/nta-ccms/backend/auth-access-service/pom.xml spring-boot:run
mvn -f apps/nta-ccms/backend/master-data-service/pom.xml spring-boot:run
mvn -f apps/nta-ccms/backend/ticket-service/pom.xml spring-boot:run
mvn -f apps/nta-ccms/backend/workflow-service/pom.xml spring-boot:run
```
