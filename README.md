# InvestLedger

InvestLedger is a fintech portfolio management app that lets advisers and investors track holdings, transactions, and portfolio performance. Built with SQL, Java Spring Boot, and Delphi — inspired by enterprise investment wrap platforms.

The MVP covers investors/advisers, portfolios, holdings, transactions, and portfolio/investor summary views.
## Project Structure

- `db/migrations`: SQL migrations and seed data (portable source files)
- `api`: Spring Boot backend (Flyway uses `api/src/main/resources/db/migration`)
- `client-delphi/src`: Delphi VCL skeleton client

## Quick start (Docker)

The only requirement is Docker with Compose. From the repository root:

```shell
docker compose up --build
```

This starts PostgreSQL and the API, and Flyway applies the schema and seed data on first run. Once it's up, open **http://localhost:8080/dashboard/index.html** and enter API key `dev-investledger-key`.

Stop with `docker compose down`. Add `-v` to also delete the database and start fresh next time.

To use a different API key, set `INVESTLEDGER_API_KEY` before starting. Compose is for local development: it uses default database credentials and only accepts connections from your own machine.

## Manual setup (without Docker)

Install these anywhere, and make sure `java` and `mvn` are on your `PATH`:

- Java 17+
- Maven 3.9+
- PostgreSQL 14+

To keep Maven's download cache on another drive, set `MAVEN_OPTS` to `-Dmaven.repo.local=<path-to-cache>`.

### 1) Create database

With PostgreSQL running:

```sql
CREATE DATABASE investledger;
```

The API connects to `localhost:5432` as `postgres` / `postgres` by default. To change that, set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD`.

### 2) Schema + seed files

Migrations are in `api/src/main/resources/db/migration`. Flyway runs them automatically when the API starts.

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

## Spring Boot API

### Run API

Skip this if you're using Docker. Otherwise, after completing the manual setup:

```shell
cd api
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

Example create portfolio (macOS/Linux):

```shell
curl -X POST http://localhost:8080/api/portfolios \
  -H "X-API-Key: dev-investledger-key" \
  -H "Content-Type: application/json" \
  -d '{"investorId":1,"name":"New Portfolio","currency":"AUD","status":"ACTIVE"}'
```

Windows PowerShell:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/portfolios `
  -Headers @{ "X-API-Key" = "dev-investledger-key" } `
  -ContentType "application/json" `
  -Body '{"investorId":1,"name":"New Portfolio","currency":"AUD","status":"ACTIVE"}'
```

### Run tests

```shell
cd api
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
