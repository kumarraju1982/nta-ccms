# NTA CCMS Local Setup

Date: 2026-03-17

Related reference:

- `apps/nta-ccms/docs/digit-command-reference.md`

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

## 2.0 DIGIT Integration Variables (Required)

Set these before starting services:

```env
CCMS_TENANT_ID=pb.amritsar

CCMS_DIGIT_AUTH_BASE_URL=http://localhost:8080
CCMS_DIGIT_AUTH_TOKEN_PATH=/keycloak/realms/master/protocol/openid-connect/token
CCMS_DIGIT_AUTH_USERINFO_PATH=/keycloak/realms/master/protocol/openid-connect/userinfo
CCMS_DIGIT_AUTH_CLIENT_ID=admin-cli
CCMS_DIGIT_AUTH_CLIENT_SECRET=

CCMS_DIGIT_MDMS_BASE_URL=http://localhost:8099/mdms-v2
CCMS_DIGIT_MDMS_SEARCH_PATH=/v1/mdms
CCMS_MDMS_MODULE_NAME=ccms
CCMS_MDMS_EXAMS_MASTER=exams
CCMS_MDMS_CATEGORIES_MASTER=queryCategories

CCMS_DIGIT_WORKFLOW_BASE_URL=http://localhost:8085/workflow
CCMS_DIGIT_WORKFLOW_PROCESS_PATH=/v1/process
CCMS_DIGIT_WORKFLOW_TRANSITION_PATH=/v1/transition
CCMS_DIGIT_WORKFLOW_PROCESS_CODE=CCMS_TICKET

CCMS_DIGIT_NOTIFICATION_ENABLED=false
CCMS_DIGIT_NOTIFICATION_BASE_URL=http://localhost:8091/notification
CCMS_DIGIT_NOTIFICATION_SMS_PATH=/v1/sms/send
CCMS_DIGIT_NOTIFICATION_TEMPLATE_ID=ccms-ticket-status
```

Note: `ticket-service` sends `toStatus` as DIGIT workflow `action`. Ensure DIGIT process actions are configured with matching names.

Run one-time local DIGIT bootstrap (creates CCMS workflow + MDMS masters):

```bash
powershell -ExecutionPolicy Bypass -File apps/nta-ccms/scripts/bootstrap-digit-local.ps1
```

## 2.1 PostgreSQL Requirement for Ticket Service

Create database:

```sql
CREATE DATABASE nta_ccms;
```

Ticket service DB variables (optional, defaults shown):

```env
CCMS_TICKET_DB_URL=jdbc:postgresql://localhost:5434/nta_ccms
CCMS_TICKET_DB_USERNAME=postgres
CCMS_TICKET_DB_PASSWORD=password
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
2. Login with DIGIT-authenticated username/password (local default: `admin` / `admin`)
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

## 5.2 One-Command End-to-End Smoke

```bash
powershell -ExecutionPolicy Bypass -File apps/nta-ccms/scripts/smoke-test-local.ps1
```

## 6. Seed Data

Master seed sample is available at:

- `apps/nta-ccms/backend/seeds/masters.sample.json`
