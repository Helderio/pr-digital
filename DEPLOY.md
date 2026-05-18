# PR Digital Deploy

## Backend: Render

Use `render.yaml` from this repository root.

Set these environment variables in Render:

- `SPRING_DATASOURCE_URL`: `jdbc:postgresql://<aiven-host>:<port>/<database>?sslmode=require`
- `SPRING_DATASOURCE_USERNAME`: Aiven PostgreSQL user
- `SPRING_DATASOURCE_PASSWORD`: Aiven PostgreSQL password
- `JWT_SECRET`: strong 32+ character secret
- `FIRECRAWL_API_KEY`: Firecrawl key
- `CORS_ALLOWED_ORIGINS`: production frontend origin, for example `https://prdigital.vercel.app`

Render health check path: `/actuator/health`.

## Database: Aiven PostgreSQL

Create PostgreSQL 15+ and copy the connection details. For JDBC, use the `jdbc:postgresql://` form with `sslmode=require`.

Example:

```text
jdbc:postgresql://pg-xxxxx.aivencloud.com:12345/defaultdb?sslmode=require
```

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
