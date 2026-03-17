# NTA CCMS Frontend (React)

React frontend for NTA CCMS with gateway-first and service-wise URL fallback.

## Run

```bash
cd apps/nta-ccms-frontend
npm install
npm run dev
```

## API Configuration

Use `.env` values from `.env.example`.

Routing behavior:

1. If `REACT_APP_GATEWAY_URL` is set, frontend uses gateway.
2. Otherwise frontend calls individual service URLs.
