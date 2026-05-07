# OngHub

Monorepo for the OngHub web system (TCC2). This repository contains a Spring Boot backend and a React + Vite frontend.

## Structure

- `backend/` Spring Boot API (Java 21)
- `frontend/` React SPA (Vite + TypeScript)
- `docker-compose.yml` local stack

## Quick start (dev)

```powershell
cd backend
.\mvnw spring-boot:run
```

```powershell
cd frontend
npm install
npm run dev
```

## Tests

```powershell
cd backend
.\mvnw test
```

This README will be expanded with full documentation as the modules are completed.
