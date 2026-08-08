# portfolio-api

Backend for [gurindersingh.dev](https://gurindersingh.dev). Spring Boot 3.3, Java 21, PostgreSQL.

## What it does

| Endpoint | Method | Purpose |
|---|---|---|
| `/api/projects` | GET | Project records, ordered for display |
| `/api/contact` | POST | Contact form submission |
| `/docs` | GET | Swagger UI |
| `/api-docs` | GET | OpenAPI JSON |
| `/actuator/health` | GET | Health check |

## Design notes

**Rate limiting.** The contact endpoint allows 3 submissions per IP per hour, using a
Bucket4j token bucket held in memory. In-memory is a deliberate choice for a single-instance
deployment — adding Redis for a portfolio contact form would be cost without benefit. The
`RateLimiter` interface is narrow enough that swapping in a distributed store later is a
one-class change.

**Client IP.** Behind Cloudflare, `request.getRemoteAddr()` returns Cloudflare's edge IP,
not the visitor's. The controller reads `CF-Connecting-IP` first, falling back to
`X-Forwarded-For`, then the socket address.

**Honeypot.** `ContactRequest` carries a `website` field that is hidden from real users
via CSS. Bots fill every field they find. Submissions with it populated get a 202 and are
silently discarded — returning an error would just tell the bot to try again differently.

**IP storage.** Source IPs are SHA-256 hashed before storage. Enough to correlate abuse,
not enough to be a personal data liability.

**Email is best-effort.** Messages are persisted first, then a notification is attempted.
If SMTP fails the request still succeeds, because the message is already safe in the
database and a 500 would lose it from the sender's perspective.

**Schema migrations.** Flyway, with `ddl-auto: validate`. Hibernate never modifies the
schema; it only checks the entities match what the migrations built. Schema changes are
explicit, reviewable, and versioned.

## Running locally

Requires Java 21 and PostgreSQL.

```bash
createdb portfolio
cp .env.example .env      # then edit
mvn spring-boot:run
```

Swagger UI: http://localhost:8080/docs

## Tests

```bash
mvn test
```

Integration tests run against in-memory H2 in PostgreSQL compatibility mode, covering
validation, the honeypot, and rate limit enforcement.

## Environment variables

| Variable | Required | Notes |
|---|---|---|
| `DATABASE_URL` | yes | JDBC URL |
| `DATABASE_USER` | yes | |
| `DATABASE_PASSWORD` | yes | |
| `ALLOWED_ORIGINS` | no | Comma separated; defaults to the production domains |
| `MAIL_HOST` / `MAIL_PORT` | no | Omit to disable email notification |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | no | Gmail requires an app password, not the account password |
| `NOTIFY_TO` / `NOTIFY_FROM` | no | Where contact notifications are sent |
| `PORT` | no | Defaults to 8080 |

## Deployment

Dockerfile builds a multi-stage image running as a non-root user. Any container host works
— Railway, Render, Fly.io, or a VPS.

Note that free tiers idle containers out after inactivity, so the first request after a
quiet period can take 30–60 seconds. The site is static and does not depend on this API to
render, which keeps that cold start invisible to visitors.
