# NTA CCMS Backend (DIGIT-Aligned)

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
