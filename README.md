# OngHub

**OngHub** é um sistema web gratuito e de código aberto para ONGs gerenciarem doações, campanhas e voluntariado. Este repositório é a implementação prática (**TCC2**) que dá suporte ao planejamento acadêmico do **TCC1**, desenvolvido no contexto da **UTFPR** (Universidade Tecnológica Federal do Paraná).

## Arquitetura

- **Backend**: Java 21, Spring Boot 4, Spring Security (JWT + refresh token), Spring Data JPA, Flyway, Springdoc OpenAPI (Swagger UI), PostgreSQL.
- **Frontend**: React 19, TypeScript, Vite, TanStack Query, React Hook Form + Zod, Tailwind CSS, Recharts.
- **Infraestrutura**: Docker Compose (PostgreSQL, API, SPA via imagem Nginx, proxy reverso e backups lógicos agendados).

A API segue uma estrutura clássica em três camadas: controllers delegam para services; services usam repositories; DTOs são usados na fronteira HTTP.

## Pré-requisitos

- **Java 21** e Maven, ou o `mvnw` incluído no projeto.
- **Node.js 20+** e npm para desenvolvimento local do frontend.
- **Docker** e Docker Compose, opcionalmente, para executar a stack completa.

## Início rápido

### 1. Ambiente

Configure as variáveis no arquivo `.env`, incluindo JWT, senhas do banco de dados e SMTP.

> O arquivo `.env` é ignorado pelo Git e contém as variáveis usadas pelo Docker Compose e pelas aplicações.

### 2. Desenvolvimento (backend + frontend separadamente)

**Backend**

```powershell
cd backend
.\mvnw spring-boot:run
```

API padrão: `http://localhost:8080`. Interface OpenAPI: `http://localhost:8080/swagger-ui.html` ou `/swagger-ui/index.html`, dependendo da versão do Springdoc.

**Frontend**

```powershell
cd frontend
npm install
npm run dev
```

Aponte a SPA para a API com `VITE_API_BASE_URL`, por exemplo `http://localhost:8080/api/v1` em desenvolvimento.

### 3. Stack completa com Docker Compose

Na raiz do repositório, depois de configurar o `.env`:

```powershell
docker compose up --build
```

- SPA (container): `http://localhost:5173`
- Proxy Nginx (API + SPA): `http://localhost` (`/api/` -> backend, `/` -> frontend)

O arquivo Compose inclui o serviço **`db_backup`**, que executa `pg_dump` em intervalos definidos por `BACKUP_INTERVAL_SECONDS` no `.env` e armazena dumps em **formato customizado** no volume `postgres_backups` (`/backups` dentro do container). Restaure com `pg_restore` quando necessário.

## Testes

**Backend**

```powershell
cd backend
.\mvnw test
```

**Frontend**

```powershell
cd frontend
npm test
```

Vitest + Testing Library; a cobertura inicial contempla fluxos críticos de interface, como login.

## Principais grupos da API

Prefixo de versão: `/api/v1`.

| Área | Exemplos |
|------|----------|
| Autenticação | `POST /auth/login`, `POST /auth/register`, `POST /auth/refresh` |
| Usuários | `GET/PATCH /users/me` |
| ONGs | `GET /ngos/public`, `GET /ngos/{id}/public`, rotas de gerenciamento em `/ngos` |
| Campanhas | `GET /campaigns/public`, CRUD em `/campaigns` |
| Doações | `POST /donations/financial`, `POST /donations/material`, `GET /donations/my`, `GET /donations/{id}/receipt (PDF)` |
| Voluntariado | listagens públicas em `/opportunities` (`GET /opportunities`, `GET /opportunities/{id}`), candidatura `POST /opportunities/{id}/apply`, decisões `PATCH /opportunities/{id}/applications/{applicationId}`, histórico do voluntário `GET /volunteers/my-applications` (compatibilidade: `/volunteer/...`) |
| Dashboard | `GET /dashboard/admin`, `/dashboard/ngo/{ngoId}`, `/dashboard/donor` |
| Relatórios | dashboards: `GET /reports/admin/dashboard`, `GET /reports/ngo/{ngoId}/dashboard`, `GET /reports/donor/dashboard`, relatório de campanha `GET /reports/campaigns/{campaignId}`; exportação `GET /reports/ngo/{ngoId}/export?format=csv|pdf` (compatibilidade: `GET /reports/ngos/{ngoId}/transparency.csv`, `.pdf`) |

Para consultar o contrato completo e os schemas, use o **Swagger UI** (`/swagger-ui.html`) com o backend em execução.

## HTTPS / Nginx (produção)

O arquivo `nginx/default.conf` incluído escuta na **porta 80** e encaminha requisições para `frontend` e `backend`. Para produção:

1. Termine o TLS no Nginx usando certificados da sua autoridade certificadora, por exemplo Let's Encrypt.
2. Adicione um bloco `server { listen 443 ssl; ... }` com `ssl_certificate` e `ssl_certificate_key`.
3. Opcionalmente, redirecione HTTP -> HTTPS com `return 301 https://$host$request_uri`.
4. Defina `ALLOWED_ORIGINS` e `APP_BASE_URL` com as URLs públicas HTTPS.

Mantenha segredos JWT e senhas do banco de dados apenas em variáveis de ambiente ou em um gerenciador de segredos, nunca no Git.

## Estrutura do repositório

- `backend/` -> aplicação Spring Boot, com migrations Flyway em `src/main/resources/db/migration/`.
- `frontend/` -> SPA Vite React (`src/features/*` por domínio, UI compartilhada em `src/components/`).
- `nginx/` -> configuração do proxy reverso para Compose.
- `docker-compose.yml` -> Postgres, API, imagem do frontend, Nginx e sidecar de backup.

## Licença

Este projeto está licenciado sob a **Licença MIT**. Consulte [LICENSE](LICENSE).
