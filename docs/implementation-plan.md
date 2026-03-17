# NTA CCMS Implementation Plan

Date: 2026-03-17  
Project: National Testing Agency (NTA) Call Centre Management System (CCMS)  
Primary Business Term: Candidate

## 1. Objective

Build a production-grade AI-enabled Call Centre Management System for NTA with:

- DIGIT-aligned backend architecture
- React frontend
- Modular, scalable, secure foundation
- Role-based workspaces for Admin, Agent, Officer, Candidate (and QA phase-2)
- Ticket lifecycle, workflow, notifications, dashboards, and AI-assisted grouping

All business-facing usage uses the term `Candidate`.

## 2. Solution Architecture

CCMS is designed as a DIGIT-style distributed service ecosystem.

Core services:

- `ccms-api-gateway` (optional in local, preferred in higher environments)
- `ccms-auth-access-service`
- `ccms-master-data-service`
- `ccms-candidate-service`
- `ccms-call-intake-service`
- `ccms-transcript-service`
- `ccms-ticket-service`
- `ccms-workflow-service`
- `ccms-grouping-service`
- `ccms-notification-service`
- `ccms-reporting-service`
- `ccms-audit-service`

Platform components:

- PostgreSQL (service-owned schema boundaries)
- Kafka for async events/jobs
- Redis for cache/session/rate limits
- Object storage for recordings/documents
- Optional OpenSearch for advanced transcript search

## 3. DIGIT-Aligned Distributed Integration

### 3.1 Service Connectivity Model

Design assumptions:

- Services are distributed and may run on different hosts/ports in local/dev.
- Frontend must not hardcode a single base URL.
- Gateway routing is preferred in higher environments.
- Direct service URLs are supported in local development.

### 3.2 Frontend Environment Configuration

Required frontend configuration variables:

- `REACT_APP_GATEWAY_URL`
- `REACT_APP_USER_SERVICE_URL`
- `REACT_APP_MDMS_SERVICE_URL`
- `REACT_APP_WORKFLOW_SERVICE_URL`
- `REACT_APP_GRIEVANCE_SERVICE_URL`
- `REACT_APP_NOTIFICATION_SERVICE_URL`
- `REACT_APP_REPORTING_SERVICE_URL`

Additional service URLs introduced for CCMS:

- `REACT_APP_CANDIDATE_SERVICE_URL`
- `REACT_APP_CALL_INTAKE_SERVICE_URL`
- `REACT_APP_TRANSCRIPT_SERVICE_URL`
- `REACT_APP_GROUPING_SERVICE_URL`
- `REACT_APP_AUDIT_SERVICE_URL`

Resolution strategy:

1. If `REACT_APP_GATEWAY_URL` exists, frontend uses gateway paths.
2. If not, frontend calls service-specific URLs directly.

### 3.3 Local Port-Wise Expectation (Suggested)

- Gateway: `localhost:8080`
- Auth Access Service: `localhost:8081`
- Master Data Service: `localhost:8082`
- Candidate Service: `localhost:8083`
- Call Intake Service: `localhost:8084`
- Workflow Service: `localhost:8085`
- Transcript Service: `localhost:8086`
- Ticket Service: `localhost:8087`
- Grouping Service: `localhost:8088`
- Notification Service: `localhost:8089`
- Reporting Service: `localhost:8090`
- Audit Service: `localhost:8091`
- React Frontend: `localhost:3000` (or Vite dev port as configured)

These ports are environment-configurable.

## 4. Functional Module Breakdown

- Authentication and RBAC
- Master Data Management
- AI Call Intake and Telephony Adapter Layer
- Candidate Identification and Validation
- Transcript and Conversation Processing
- Ticket / Grievance Management
- Workflow and SLA/T+1 Tracking
- Similar Issue Grouping
- Notifications and Candidate Communications
- Candidate Portal
- Agent Workspace
- Officer Workspace
- Admin Console
- Dashboard and Reporting
- QA Module (Phase 2 scaffold)

## 5. Role Model

- System Admin (`DG Login`)
- Agent group (`Team Lead`, `Exam Lead`, `Call Agent`)
- Officer group (`Exam Department`, `Exam Head`)
- Candidate login
- QA login (phase-2)

Role access is enforced in backend APIs and frontend route guards.

## 6. Entity Design (Core)

Core entities:

- `User`
- `Role`
- `UserRoleMapping`
- `Candidate`
- `Exam`
- `ExamCycle`
- `Ticket`
- `TicketHistory`
- `TicketNote`
- `Attachment`
- `CallLog`
- `Transcript`
- `EscalationRule`
- `NotificationTemplate`
- `NotificationLog`
- `GroupedIssue`
- `GroupedIssueTicketMap`
- `MasterCategory`
- `MasterSubCategory`
- `AuditLog`

Audit fields on all transactional entities:

- `tenant_id`
- `created_at`
- `created_by`
- `updated_at`
- `updated_by`

### 6.1 Key Index Strategy

- `tickets(grievance_id)` unique
- `tickets(status, assigned_agent_id)`
- `tickets(status, assigned_officer_id)`
- `tickets(exam_id, category_id, created_at)`
- `tickets(sla_due_at, status)`
- `tickets(t_plus_1_due_at, status)`
- `call_logs(call_ref_id)` unique
- `transcripts(call_ref_id)`
- `notification_logs(ticket_id, created_at)`
- `grouped_issue_ticket_map(grouped_issue_id)`
- `grouped_issue_ticket_map(ticket_id)` unique

## 7. Workflow and Status Matrix

Status progression:

- `NEW`
- `IN_PROGRESS`
- `PENDING_INFO_FROM_CANDIDATE`
- `RESOLVED_BY_AGENT`
- `UNRESOLVED`
- `ESCALATED_TO_OFFICER`
- `UNDER_OFFICER_REVIEW`
- `FINAL_RESOLVED`
- `REOPENED`
- `CLOSED`

Rules:

- `RESOLVED_BY_AGENT` tickets are not shown in officer unresolved queue.
- Officer queue includes only `UNRESOLVED`, `ESCALATED_TO_OFFICER`, `UNDER_OFFICER_REVIEW`, `REOPENED`.
- Reopened tickets must re-enter active queue.
- Grouped-issue response must be officer-approved before bulk send.
- SLA and T+1 windows are computed and tracked on transitions.
- Every critical action writes `TicketHistory` and `AuditLog`.

## 8. API Inventory (v1)

Authentication and users:

- `POST /api/v1/auth/login/password`
- `POST /api/v1/auth/login/otp/request`
- `POST /api/v1/auth/login/otp/verify`
- `GET /api/v1/users/me`
- `POST /api/v1/admin/users`
- `PUT /api/v1/admin/users/{id}/roles`

Masters:

- `GET/POST/PUT /api/v1/masters/exams`
- `GET/POST/PUT /api/v1/masters/exam-cycles`
- `GET/POST/PUT /api/v1/masters/categories`
- `GET/POST/PUT /api/v1/masters/sub-categories`
- `GET/POST/PUT /api/v1/masters/escalation-rules`
- `GET/POST/PUT /api/v1/masters/notification-templates`
- `GET/POST/PUT /api/v1/masters/ticket-statuses`
- `GET/POST/PUT /api/v1/masters/reopen-reasons`

Candidate:

- `POST /api/v1/candidates/validate`
- `GET /api/v1/candidates/{id}`
- `GET /api/v1/candidates/by-mobile/{mobile}`

Call intake and transcript:

- `POST /api/v1/calls/webhook/{provider}`
- `POST /api/v1/calls/events`
- `GET /api/v1/calls/{callRefId}`
- `POST /api/v1/transcripts/ingest`
- `GET /api/v1/transcripts/{callRefId}`
- `POST /api/v1/transcripts/{callRefId}/process-ai`

Tickets:

- `POST /api/v1/tickets`
- `GET /api/v1/tickets`
- `GET /api/v1/tickets/{grievanceId}`
- `PUT /api/v1/tickets/{grievanceId}`
- `POST /api/v1/tickets/{grievanceId}/assign`
- `POST /api/v1/tickets/{grievanceId}/transition`
- `POST /api/v1/tickets/{grievanceId}/notes`
- `POST /api/v1/tickets/{grievanceId}/attachments`
- `POST /api/v1/tickets/{grievanceId}/reopen`

Grouping:

- `POST /api/v1/grouped-issues/run-detection`
- `GET /api/v1/grouped-issues`
- `GET /api/v1/grouped-issues/{id}`
- `PUT /api/v1/grouped-issues/{id}/response-draft`
- `POST /api/v1/grouped-issues/{id}/approve-response`
- `POST /api/v1/grouped-issues/{id}/bulk-dispatch`

Notifications:

- `POST /api/v1/notifications/trigger`
- `GET /api/v1/notifications/logs`
- `GET /api/v1/notifications/logs/{ticketId}`

Reporting:

- `GET /api/v1/reports/dashboard/agent`
- `GET /api/v1/reports/dashboard/officer`
- `GET /api/v1/reports/dashboard/admin`
- `GET /api/v1/reports/sla`
- `GET /api/v1/reports/exam-distribution`

Candidate portal:

- `POST /api/v1/candidate-portal/login/otp/request`
- `POST /api/v1/candidate-portal/login/otp/verify`
- `GET /api/v1/candidate-portal/tickets`
- `GET /api/v1/candidate-portal/tickets/{grievanceId}`
- `POST /api/v1/candidate-portal/tickets/{grievanceId}/additional-info`
- `POST /api/v1/candidate-portal/tickets/{grievanceId}/reopen`

## 9. Frontend Screen Map

Common:

- Login
- Unauthorized
- Profile

Agent workspace:

- Agent Dashboard
- Assigned Queue
- Ticket Search/List
- Ticket Detail (Candidate, Transcript, Timeline, Notes, Actions)

Officer workspace:

- Officer Dashboard
- Escalated/Unresolved Queue
- Grouped Issue Review
- Common Response Approval
- Final Resolution Dispatch

Admin console:

- Admin Dashboard
- User and Role Management
- Master Data Management
- Escalation Rules
- Template Management
- Integration Settings
- Audit Trail
- Reports

Candidate portal:

- OTP Login
- My Tickets
- Ticket Status Detail
- Additional Info Upload
- Reopen Ticket
- Communication History

QA (Phase 2):

- QA Queue
- QA Review
- QA Reports

## 10. AI and Telephony Abstraction Design

Provider-agnostic interfaces:

- Speech-to-text adapter
- Summarization adapter
- Classification adapter
- Keyword extraction adapter
- Similar issue detection adapter
- Response suggestion adapter

Telephony adapters:

- Twilio adapter
- Airtel IQ adapter
- Exotel adapter
- Generic webhook adapter fallback

Design requirements:

- Confidence score support
- Human override support
- Fallback behavior when AI fails
- No provider lock-in in domain logic

## 11. Non-Functional Requirements

Performance:

- Support approximately 20,000 calls/day
- Pagination and optimized search queries
- Indexed reporting paths

Security:

- RBAC and API authorization
- Strict input validation
- Secrets externalized
- Sensitive-field masking
- Auditable critical actions

Scalability:

- Service boundaries maintained
- Async processing for transcript/notification/grouping

Observability:

- Structured logs
- Health endpoints
- Error classification
- Integration failure visibility

Maintainability:

- Clean modular boundaries
- Reusable frontend components/hooks/services
- Configuration-driven behavior

## 12. Phased Implementation Strategy

Phase 1: Foundation

1. Repository structure and service contracts
2. Auth and RBAC
3. Master data module and seed data
4. API config and gateway/direct routing support

Phase 2: Core Ticketing

1. Candidate validation and call intake ingestion
2. Ticket create/search/detail/update APIs
3. Workflow transitions and SLA tracking
4. Agent workspace screens

Phase 3: Escalation and Officer Flow

1. Officer unresolved/escalated queue
2. Final resolution flow
3. Candidate reopen/additional info
4. Notification templates and logs

Phase 4: AI and Grouping

1. AI abstraction and mock providers
2. Transcript AI processing
3. Grouped issue detection/review/approval
4. Bulk communication for approved grouped responses

Phase 5: Hardening and Reporting

1. Dashboard/report APIs and UI
2. Performance and index tuning
3. Security and audit hardening
4. QA module scaffold completion

## 13. Assumptions

- DIGIT core patterns are followed; exact shared service dependencies may vary by environment.
- Single tenant can be used for initial rollout; multi-tenant fields remain in schema.
- Real telephony/AI providers are integrated after MVP via adapter implementations.
- Candidate communication channels depend on provider credentials and regulatory approvals.

## 14. Future Enhancements

- Advanced semantic clustering and retrieval-augmented response drafting
- Predictive SLA breach alerts
- QA auto-scoring models
- Exam-cycle anomaly detection
- Multilingual transcript quality improvements
