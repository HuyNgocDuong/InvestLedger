CREATE TABLE investor (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE adviser (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE investor_adviser (
    investor_id BIGINT NOT NULL REFERENCES investor(id),
    adviser_id BIGINT NOT NULL REFERENCES adviser(id),
    role VARCHAR(30) NOT NULL DEFAULT 'PRIMARY',
    since_date DATE NOT NULL,
    PRIMARY KEY (investor_id, adviser_id)
);

CREATE TABLE portfolio (
    id BIGSERIAL PRIMARY KEY,
    investor_id BIGINT NOT NULL REFERENCES investor(id),
    name VARCHAR(150) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE instrument (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    instrument_type VARCHAR(20) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    current_price NUMERIC(18, 4) NOT NULL DEFAULT 0,
    price_as_of TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE holding (
    id BIGSERIAL PRIMARY KEY,
    portfolio_id BIGINT NOT NULL REFERENCES portfolio(id),
    instrument_id BIGINT NOT NULL REFERENCES instrument(id),
    quantity NUMERIC(20, 6) NOT NULL DEFAULT 0,
    average_cost NUMERIC(18, 4) NOT NULL DEFAULT 0,
    market_value NUMERIC(18, 4) NOT NULL DEFAULT 0,
    UNIQUE (portfolio_id, instrument_id)
);

CREATE TABLE portfolio_transaction (
    id BIGSERIAL PRIMARY KEY,
    portfolio_id BIGINT NOT NULL REFERENCES portfolio(id),
    instrument_id BIGINT NOT NULL REFERENCES instrument(id),
    trade_date DATE NOT NULL,
    settlement_date DATE,
    transaction_type VARCHAR(20) NOT NULL,
    quantity NUMERIC(20, 6) NOT NULL DEFAULT 0,
    price NUMERIC(18, 4) NOT NULL DEFAULT 0,
    gross_amount NUMERIC(18, 4) NOT NULL DEFAULT 0,
    net_amount NUMERIC(18, 4) NOT NULL DEFAULT 0,
    currency VARCHAR(3) NOT NULL,
    notes VARCHAR(500)
);

CREATE INDEX idx_portfolio_investor ON portfolio(investor_id);
CREATE INDEX idx_holding_portfolio ON holding(portfolio_id);
CREATE INDEX idx_holding_instrument ON holding(instrument_id);
CREATE INDEX idx_txn_portfolio ON portfolio_transaction(portfolio_id);
CREATE INDEX idx_txn_instrument ON portfolio_transaction(instrument_id);
