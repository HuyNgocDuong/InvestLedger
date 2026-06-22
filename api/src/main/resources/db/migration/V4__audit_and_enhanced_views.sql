CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL,
    adviser_id BIGINT REFERENCES adviser(id),
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_log(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_created ON audit_log(created_at DESC);

DROP VIEW IF EXISTS investor_summary_view;
DROP VIEW IF EXISTS portfolio_summary_view;

CREATE VIEW portfolio_summary_view AS
SELECT
    p.id AS portfolio_id,
    p.investor_id,
    COALESCE(SUM(CASE WHEN i.symbol <> 'CASH' THEN h.market_value ELSE 0 END), 0) AS invested_market_value,
    COALESCE(SUM(h.market_value), 0) AS total_market_value,
    COALESCE(SUM(CASE WHEN i.symbol <> 'CASH' THEN h.quantity * h.average_cost ELSE 0 END), 0) AS total_cost,
    COALESCE(SUM(CASE WHEN i.symbol <> 'CASH' THEN h.market_value ELSE 0 END), 0)
        - COALESCE(SUM(CASE WHEN i.symbol <> 'CASH' THEN h.quantity * h.average_cost ELSE 0 END), 0) AS unrealized_pnl,
    COALESCE(MAX(CASE WHEN i.symbol = 'CASH' THEN h.quantity ELSE 0 END), 0) AS cash_balance
FROM portfolio p
LEFT JOIN holding h ON h.portfolio_id = p.id
LEFT JOIN instrument i ON i.id = h.instrument_id
GROUP BY p.id, p.investor_id;

CREATE VIEW investor_summary_view AS
SELECT
    i.id AS investor_id,
    COALESCE(SUM(ps.total_market_value), 0) AS total_market_value,
    COALESCE(SUM(ps.total_cost), 0) AS total_cost,
    COALESCE(SUM(ps.unrealized_pnl), 0) AS unrealized_pnl,
    COALESCE(SUM(ps.cash_balance), 0) AS cash_balance
FROM investor i
LEFT JOIN portfolio_summary_view ps ON ps.investor_id = i.id
GROUP BY i.id;
