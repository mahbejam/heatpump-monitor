# heatpump-monitor

A small remote-monitoring demo for heat pumps, built as a learning project.
Inspired by remote maintenance platforms for heat pumps. It is not affiliated
with any manufacturer.

**Stack:** Kotlin + Spring Boot (backend), React + TypeScript (dashboard),
PostgreSQL, Docker, Kubernetes.

## Architecture (target)

```
[Simulator] --POST--> [Kotlin Backend (REST API)] <--GET-- [React Dashboard]
                              |
                         [PostgreSQL]
```

## Status

- [x] Day 1: Backend skeleton, `GET /api/devices`, health endpoint, tests
- [ ] Day 2: PostgreSQL + persistence
- [ ] Day 3: Measurements endpoints
- [ ] Day 4-5: React + TypeScript dashboard
- [ ] Day 6: Docker
- [ ] Day 7-8: Kubernetes (kind) + probes
- [ ] Day 9: Simple alerts

## Run the backend

Requirements: JDK 21

```bash
cd backend
./gradlew bootRun      # Windows: gradlew.bat bootRun
```

Then open:

- http://localhost:8080/api/devices
- http://localhost:8080/api/devices/1
- http://localhost:8080/actuator/health

## Run the tests

```bash
cd backend
./gradlew test         # Windows: gradlew.bat test
```

## API

| Method | Path | Description |
|---|---|---|
| GET | `/api/devices` | List all devices |
| GET | `/api/devices/{id}` | One device (404 if unknown) |
| GET | `/actuator/health` | Health check |
