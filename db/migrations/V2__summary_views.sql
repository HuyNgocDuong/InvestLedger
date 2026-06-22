CREATE OR REPLACE VIEW portfolio_summary_view AS
SELECT
    p.id AS portfolio_id,
    p.investor_id,
    COALESCE(SUM(h.market_value), 0) AS total_market_value,
    COALESCE(SUM(h.quantity * h.average_cost), 0) AS total_cost,
    COALESCE(SUM(h.market_value), 0) - COALESCE(SUM(h.quantity * h.average_cost), 0) AS unrealized_pnl
FROM portfolio p
LEFT JOIN holding h ON h.portfolio_id = p.id
GROUP BY p.id, p.investor_id;

CREATE OR REPLACE VIEW investor_summary_view AS
SELECT
    i.id AS investor_id,
    COALESCE(SUM(ps.total_market_value), 0) AS total_market_value,
    COALESCE(SUM(ps.total_cost), 0) AS total_cost,
    COALESCE(SUM(ps.unrealized_pnl), 0) AS unrealized_pnl
FROM investor i
LEFT JOIN portfolio_summary_view ps ON ps.investor_id = i.id
GROUP BY i.id;
