# NTA CCMS Local Setup

Date: 2026-03-17

## 1. Services and Ports

- Auth Access Service: `8081`
- Master Data Service: `8082`
- Workflow Service: `8085`
- Ticket Service: `8087`
- Frontend (React): `3000`

## 2. Backend Run Commands

From repo root:

```bash
mvn -f apps/nta-ccms/backend/auth-access-service/pom.xml spring-boot:run
mvn -f apps/nta-ccms/backend/master-data-service/pom.xml spring-boot:run
mvn -f apps/nta-ccms/backend/workflow-service/pom.xml spring-boot:run
mvn -f apps/nta-ccms/backend/ticket-service/pom.xml spring-boot:run
```

## 2.1 PostgreSQL Requirement for Ticket Service

Create database:

```sql
CREATE DATABASE nta_ccms;
```

Ticket service DB variables (optional, defaults shown):

```env
CCMS_TICKET_DB_URL=jdbc:postgresql://localhost:5432/nta_ccms
CCMS_TICKET_DB_USERNAME=postgres
CCMS_TICKET_DB_PASSWORD=postgres
```

Flyway runs automatically on startup for `ticket-service`.

## 3. Frontend Configuration

Create `.env` in `apps/nta-ccms/frontend` based on `.env.example`.

Direct service mode example:

```env
REACT_APP_GATEWAY_URL=
REACT_APP_USER_SERVICE_URL=http://localhost:8081
REACT_APP_MDMS_SERVICE_URL=http://localhost:8082
REACT_APP_WORKFLOW_SERVICE_URL=http://localhost:8085
REACT_APP_GRIEVANCE_SERVICE_URL=http://localhost:8087
REACT_APP_NOTIFICATION_SERVICE_URL=http://localhost:8089
REACT_APP_REPORTING_SERVICE_URL=http://localhost:8090
REACT_APP_CANDIDATE_SERVICE_URL=http://localhost:8083
REACT_APP_CALL_INTAKE_SERVICE_URL=http://localhost:8084
REACT_APP_TRANSCRIPT_SERVICE_URL=http://localhost:8086
REACT_APP_GROUPING_SERVICE_URL=http://localhost:8088
REACT_APP_AUDIT_SERVICE_URL=http://localhost:8091
```

Gateway mode example:

```env
REACT_APP_GATEWAY_URL=http://localhost:8080
```

## 4. Frontend Run

```bash
cd apps/nta-ccms/frontend
npm install
npm run dev
```

## 5. Baseline Flow

1. Open frontend `http://localhost:3000`
2. Login with any username/password (mock auth)
3. View dashboard exam count
4. Open tickets page
5. Create a Candidate ticket and verify it appears in list

## 5.1 Workflow API Smoke Calls

Assign:

```bash
curl -X POST http://localhost:8087/api/v1/tickets/GRV-2026-0001/assign \
  -H "Content-Type: application/json" \
  -d "{\"assignedAgent\":\"agent1\",\"assignedOfficer\":\"officer1\",\"actionBy\":\"team-lead\"}"
```

Transition:

```bash
curl -X POST http://localhost:8087/api/v1/tickets/GRV-2026-0001/transition \
  -H "Content-Type: application/json" \
  -d "{\"toStatus\":\"IN_PROGRESS\",\"actionBy\":\"agent1\",\"remarks\":\"Started review\"}"
```

History:

```bash
curl http://localhost:8087/api/v1/tickets/GRV-2026-0001/history
```

## 6. Seed Data

Master seed sample is available at:

- `apps/nta-ccms/backend/seeds/masters.sample.json`
