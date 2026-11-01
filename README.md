# InvestLedger

InvestLedger is a fintech portfolio management app that lets advisers and investors track holdings, transactions, and portfolio performance. Built with SQL, Java Spring Boot, and Delphi — inspired by enterprise investment wrap platforms.

The MVP covers investors/advisers, portfolios, holdings, transactions, and portfolio/investor summary views.
## Project Structure

- `db/migrations`: SQL migrations and seed data (portable source files)
- `api`: Spring Boot backend (Flyway uses `api/src/main/resources/db/migration`)
- `client-delphi/src`: Delphi VCL skeleton client

## Windows setup on D: drive (recommended if C: is low on space)

This project can run with all dev tools stored on `D:\dev` and `D:\PostgreSQL`.

### Installed locations

| Tool | Path |
|------|------|
| JDK 17 | `D:\dev\jdk-17.0.19+10` |
| Maven 3.9.9 | `D:\dev\apache-maven-3.9.9` |
| Maven cache | `D:\dev\.m2\repository` |
| PostgreSQL 17 (portable) | `D:\PostgreSQL\17` |

### Environment variables (user)

- `JAVA_HOME` = `D:\dev\jdk-17.0.19+10`
- `MAVEN_HOME` = `D:\dev\apache-maven-3.9.9`
- `MAVEN_OPTS` = `-Dmaven.repo.local=D:\dev\.m2\repository`
- Add to `Path`: `%JAVA_HOME%\bin` and `%MAVEN_HOME%\bin`

Open a **new terminal** after changing env vars.

### Start PostgreSQL (before API)

```powershell
D:\dev\start-postgres.bat
```

Stop when finished:

```powershell
D:\dev\stop-postgres.bat
```

Database: `investledger`  
User/password: `postgres` / `postgres`

## Database (PostgreSQL)

### 1) Create database

```sql
CREATE DATABASE investledger;
```

### 2) Schema + seed files

Files:
- `V1__init_schema.sql`
- `V2__summary_views.sql`
- `V3__seed_data.sql`

Flyway runs them automatically when the API starts.

## What this solves (beyond a student demo)

InvestLedger now behaves more like a real adviser portfolio/wrap platform:

- **Transaction ledger engine** — holdings are rebuilt from transaction history (not manually faked)
- **Business rules** — blocks invalid sells/withdrawals when insufficient holdings
- **Adviser scoping** — advisers only see their assigned investors when `X-Adviser-Id` is sent
- **Audit trail** — portfolio/transaction changes logged to `audit_log`
- **Richer reporting** — cash balance, allocation weights, invested vs total value
- **Adviser web dashboard** — deployable UI at `/dashboard/index.html`

### Headers

| Header | Purpose |
|--------|---------|
| `X-API-Key` | Required for all `/api/**` calls |
| `X-Adviser-Id` | Optional — scopes data to that adviser's book |

### New / updated endpoints

- `GET /api/advisers/{id}/investors`
- `GET /api/audit` — recent change log
- `DELETE /api/portfolios/{id}/transactions/{transactionId}`
- `POST /api/portfolios/{id}/holdings/rebuild` — manual ledger rebuild
- Portfolio summary now includes `cashBalance`, `allocations`, `investedMarketValue`

### Adviser dashboard

After starting the API, open in a browser:

**http://localhost:8080/dashboard/index.html**

Enter API key `dev-investledger-key` (local default), click **Refresh**, and pick an adviser to demo scoped access. The dashboard never loads the key from the server, so deployed instances require the key configured for that environment.

## Spring Boot API (Windows-Friendly)

### Prerequisites

- Java 17+
- Maven 3.9+
- PostgreSQL 14+

### Configure connection

Update `api/src/main/resources/application.yml` if needed:
- URL: `jdbc:postgresql://localhost:5432/investledger`
- username/password

### Run API

From PowerShell:

```powershell
cd .\api
mvn spring-boot:run
```

Server starts at `http://localhost:8080`.

### API key authentication

All `/api/**` endpoints require the header:

```http
X-API-Key: dev-investledger-key
```

Configure the key in `api/src/main/resources/application.yml`:

```yaml
investledger:
  api-key: dev-investledger-key
```

Set `api-key` to blank to disable auth (useful for local experiments only).

`dev-investledger-key` is public and for local development only. Deployed instances must set `INVESTLEDGER_API_KEY` to a long random value, and that value must never be served to clients or committed.

### Useful endpoints

**Read**
- `GET /api/investors`
- `GET /api/investors/{id}/portfolios`
- `GET /api/investors/{id}/summary`
- `GET /api/portfolios`
- `GET /api/portfolios/{id}`
- `GET /api/portfolios/{id}/holdings`
- `GET /api/portfolios/{id}/transactions`
- `GET /api/portfolios/{id}/summary`
- `GET /api/advisers`
- `GET /api/instruments`

**Write**
- `POST /api/portfolios` — create portfolio
- `PUT /api/portfolios/{id}` — update portfolio
- `POST /api/portfolios/{id}/transactions` — create transaction
- `PUT /api/portfolios/{id}/transactions/{transactionId}` — update transaction

Example create portfolio:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/portfolios `
  -Headers @{ "X-API-Key" = "dev-investledger-key" } `
  -ContentType "application/json" `
  -Body '{"investorId":1,"name":"New Portfolio","currency":"AUD","status":"ACTIVE"}'
```

### Run tests

```powershell
cd .\api
mvn test
```

## Delphi Client (Object Pascal, VCL)

### Prerequisites

- RAD Studio / Delphi (Community or higher)

### Run

1. Open `client-delphi/src/InvestLedgerClient.dpr` in Delphi.
2. Build and Run.
3. If the client connects to the deployed API, enter its API key and click **Refresh**. If it falls back to `http://localhost:8080`, it uses `dev-investledger-key` and loads automatically.

The main form shows four grids:
- **Investors** (top-left) — select a row to load portfolios
- **Portfolios** (top-right) — select a row to load holdings/transactions
- **Holdings** (bottom-left)
- **Transactions** (bottom-right)

Summary totals appear in the header bar.

## Notes

- This MVP is intentionally lightweight for student portfolio use.
- API key authentication protects `/api/**` endpoints (see `application.yml`).
- Holdings are derived from transaction replay via the ledger engine.
