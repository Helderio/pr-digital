# PR Digital Deploy

## Backend: Render

Use `render.yaml` from this repository root.

Set these environment variables in Render:

- `JWT_SECRET`: strong 32+ character secret
- `FIRECRAWL_API_KEY`: Firecrawl key
- `CORS_ALLOWED_ORIGINS`: production frontend origin, for example `https://prdigital.vercel.app`

Render health check path: `/actuator/health`.

## Database: Render PostgreSQL

We now use a managed PostgreSQL database natively defined in the `render.yaml` Blueprint.
Render will automatically provision the database instance (`prdigital-db`) and inject the credentials directly into the backend service at runtime.
No manual database setup or copy-pasting is required.

## Frontend: Vercel

Deploy `../prdigital-essentials` as the Vercel project root.

Set:

- `VITE_API_BASE_URL=https://<your-render-service>.onrender.com/api/v1`

Vite exposes browser-side environment variables only when they are prefixed with `VITE_`.

## Local Backend

This repo includes a lightweight Maven wrapper:

```bash
./mvnw test
./mvnw spring-boot:run
```

Docker local stack:

```bash
docker compose up --build
```
