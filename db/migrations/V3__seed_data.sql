INSERT INTO adviser (first_name, last_name, email) VALUES
('Alice', 'Chen', 'alice.chen@investledger.local'),
('Michael', 'Nguyen', 'michael.nguyen@investledger.local');

INSERT INTO investor (first_name, last_name, email) VALUES
('John', 'Smith', 'john.smith@investledger.local'),
('Priya', 'Patel', 'priya.patel@investledger.local'),
('Lucas', 'Brown', 'lucas.brown@investledger.local');

INSERT INTO investor_adviser (investor_id, adviser_id, role, since_date) VALUES
(1, 1, 'PRIMARY', '2024-01-10'),
(2, 1, 'PRIMARY', '2024-02-01'),
(3, 2, 'PRIMARY', '2024-03-01');

INSERT INTO portfolio (investor_id, name, currency, status) VALUES
(1, 'John Growth Portfolio', 'AUD', 'ACTIVE'),
(1, 'John Income Portfolio', 'AUD', 'ACTIVE'),
(2, 'Priya Balanced Portfolio', 'AUD', 'ACTIVE'),
(3, 'Lucas Starter Portfolio', 'AUD', 'ACTIVE');

INSERT INTO instrument (symbol, name, instrument_type, currency, current_price, price_as_of) VALUES
('AAPL', 'Apple Inc.', 'STOCK', 'USD', 211.3400, CURRENT_TIMESTAMP),
('MSFT', 'Microsoft Corp.', 'STOCK', 'USD', 439.1200, CURRENT_TIMESTAMP),
('VAS.AX', 'Vanguard Australian Shares ETF', 'ETF', 'AUD', 98.1500, CURRENT_TIMESTAMP),
('IVV.AX', 'iShares S&P 500 ETF', 'ETF', 'AUD', 56.8000, CURRENT_TIMESTAMP),
('CASH', 'Cash Position', 'CASH', 'AUD', 1.0000, CURRENT_TIMESTAMP);

INSERT INTO holding (portfolio_id, instrument_id, quantity, average_cost, market_value) VALUES
(1, 1, 10.000000, 195.1000, 2113.4000),
(1, 3, 50.000000, 92.4000, 4907.5000),
(1, 5, 1200.000000, 1.0000, 1200.0000),
(2, 3, 80.000000, 90.2000, 7852.0000),
(2, 4, 45.000000, 52.0000, 2556.0000),
(2, 5, 900.000000, 1.0000, 900.0000),
(3, 2, 6.000000, 410.5000, 2634.7200),
(3, 4, 70.000000, 54.2000, 3976.0000),
(3, 5, 600.000000, 1.0000, 600.0000),
(4, 3, 25.000000, 96.0000, 2453.7500),
(4, 5, 300.000000, 1.0000, 300.0000);

INSERT INTO portfolio_transaction
(portfolio_id, instrument_id, trade_date, settlement_date, transaction_type, quantity, price, gross_amount, net_amount, currency, notes)
VALUES
(1, 1, '2025-01-12', '2025-01-14', 'BUY', 10.000000, 195.1000, 1951.0000, 1956.0000, 'USD', 'Initial Apple buy'),
(1, 3, '2025-02-10', '2025-02-12', 'BUY', 50.000000, 92.4000, 4620.0000, 4624.0000, 'AUD', 'VAS accumulation'),
(1, 5, '2025-02-11', '2025-02-11', 'DEPOSIT', 1200.000000, 1.0000, 1200.0000, 1200.0000, 'AUD', 'Cash deposit'),
(2, 3, '2025-03-05', '2025-03-07', 'BUY', 80.000000, 90.2000, 7216.0000, 7224.0000, 'AUD', 'Income allocation'),
(2, 4, '2025-03-20', '2025-03-22', 'BUY', 45.000000, 52.0000, 2340.0000, 2346.0000, 'AUD', 'S&P 500 exposure'),
(3, 2, '2025-01-30', '2025-02-01', 'BUY', 6.000000, 410.5000, 2463.0000, 2468.0000, 'USD', 'MSFT initial position'),
(3, 4, '2025-02-15', '2025-02-17', 'BUY', 70.000000, 54.2000, 3794.0000, 3800.0000, 'AUD', 'Core ETF buy'),
(4, 3, '2025-04-01', '2025-04-03', 'BUY', 25.000000, 96.0000, 2400.0000, 2402.0000, 'AUD', 'Starter ETF position'),
(4, 5, '2025-04-01', '2025-04-01', 'DEPOSIT', 300.000000, 1.0000, 300.0000, 300.0000, 'AUD', 'Starter cash');
