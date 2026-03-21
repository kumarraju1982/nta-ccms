# DIGIT Command Reference for NTA CCMS

Date: 2026-03-21

This reference is aligned to DIGIT tutorial document:

- `docs/tutorials/backend/Step 2_ Configuring DIGIT Service.md`

## 1) Source Commands (DIGIT CLI)

The tutorial uses `digit` CLI commands such as:

```bash
digit create-account --name Amaravati --email test@example.com --server http://localhost:8095
digit config set --server http://localhost:8095 --account AMARAVATI --client-id auth-server --client-secret changeme --username test@example.com --password default
digit create-idgen-template --template-code pgr --template "{ORG}-{DATE:yyyyMMdd}-{SEQ}-{RAND}" --scope daily --start 1 --padding-length 4 --padding-char "0" --random-length 2 --random-charset "A-Z0-9"
digit create-workflow --code PGR --default
digit create-notification-template --template-id "my-template" --version "1.0.0" --type "EMAIL" --subject "Test Subject" --content "Test Content"
digit create-boundaries --default
```

## 2) Current State on This Machine

- `digit` CLI is not installed globally (`digit --help` is not available).
- DIGIT services are running from `deploy/local/docker-compose.yml`.

## 3) CCMS Setup Equivalent (No CLI Required)

Use the project bootstrap script:

```bash
powershell -ExecutionPolicy Bypass -File apps/nta-ccms/scripts/bootstrap-digit-local.ps1
```

This script currently does:

- Upserts CCMS MDMS entries in DIGIT Postgres:
  - `ccms.exams`
  - `ccms.queryCategories`
- Creates and reuses DIGIT workflow process code `CCMS_TICKET`
- Creates required workflow states/actions for ticket lifecycle transitions

## 4) Runtime URLs Used by CCMS Integration

- Keycloak Auth: `http://localhost:8080/keycloak`
- Workflow: `http://localhost:8085/workflow`
- MDMS v2: `http://localhost:8099/mdms-v2`
- Gateway (if needed): `http://localhost:8095`

## 5) Why This Mapping Exists

The DIGIT tutorial commands are CLI wrappers over the same platform APIs.  
For CCMS local development, script-based API/DB bootstrap is currently the fastest reliable path without requiring a global CLI installation.
