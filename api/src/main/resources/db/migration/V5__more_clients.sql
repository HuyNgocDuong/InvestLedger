INSERT INTO investor (first_name, last_name, email) VALUES
('Emma', 'Wilson', 'emma.wilson@investledger.local'),
('David', 'Kim', 'david.kim@investledger.local'),
('Sarah', 'OBrien', 'sarah.obrien@investledger.local'),
('James', 'Taylor', 'james.taylor@investledger.local'),
('Mei', 'Lin', 'mei.lin@investledger.local');

INSERT INTO investor_adviser (investor_id, adviser_id, role, since_date) VALUES
(4, 1, 'PRIMARY', '2024-04-15'),
(5, 1, 'PRIMARY', '2024-05-01'),
(6, 2, 'PRIMARY', '2024-05-20'),
(7, 2, 'PRIMARY', '2024-06-10'),
(8, 1, 'PRIMARY', '2024-07-01');

INSERT INTO portfolio (investor_id, name, currency, status) VALUES
(4, 'Emma Retirement Portfolio', 'AUD', 'ACTIVE'),
(5, 'David Growth Portfolio', 'AUD', 'ACTIVE'),
(5, 'David Super Portfolio', 'AUD', 'ACTIVE'),
(6, 'Sarah Balanced Portfolio', 'AUD', 'ACTIVE'),
(7, 'James Starter Portfolio', 'AUD', 'ACTIVE'),
(8, 'Mei Income Portfolio', 'AUD', 'ACTIVE');

INSERT INTO portfolio_transaction
(portfolio_id, instrument_id, trade_date, settlement_date, transaction_type, quantity, price, gross_amount, net_amount, currency, notes)
VALUES
(5, 5, '2025-01-08', '2025-01-08', 'DEPOSIT', 5000.000000, 1.0000, 5000.0000, 5000.0000, 'AUD', 'Opening deposit'),
(5, 3, '2025-01-15', '2025-01-17', 'BUY', 40.000000, 91.5000, 3660.0000, 3665.0000, 'AUD', 'VAS core holding'),
(5, 4, '2025-02-01', '2025-02-03', 'BUY', 30.000000, 55.0000, 1650.0000, 1655.0000, 'AUD', 'IVV allocation'),
(6, 5, '2025-02-05', '2025-02-05', 'DEPOSIT', 8000.000000, 1.0000, 8000.0000, 8000.0000, 'AUD', 'Salary contribution'),
(6, 1, '2025-02-12', '2025-02-14', 'BUY', 8.000000, 198.0000, 1584.0000, 1590.0000, 'USD', 'Apple position'),
(6, 3, '2025-03-01', '2025-03-03', 'BUY', 25.000000, 93.8000, 2345.0000, 2350.0000, 'AUD', 'Local ETF'),
(7, 5, '2025-03-10', '2025-03-10', 'DEPOSIT', 3500.000000, 1.0000, 3500.0000, 3500.0000, 'AUD', 'Initial funding'),
(7, 2, '2025-03-18', '2025-03-20', 'BUY', 4.000000, 415.0000, 1660.0000, 1665.0000, 'USD', 'MSFT buy'),
(7, 4, '2025-04-05', '2025-04-07', 'BUY', 35.000000, 53.5000, 1872.5000, 1878.0000, 'AUD', 'ETF starter'),
(8, 5, '2025-04-12', '2025-04-12', 'DEPOSIT', 4200.000000, 1.0000, 4200.0000, 4200.0000, 'AUD', 'Income portfolio seed'),
(8, 3, '2025-04-20', '2025-04-22', 'BUY', 35.000000, 94.2000, 3297.0000, 3302.0000, 'AUD', 'Dividend focus ETF'),
(9, 5, '2025-05-01', '2025-05-01', 'DEPOSIT', 2500.000000, 1.0000, 2500.0000, 2500.0000, 'AUD', 'Starter cash'),
(9, 3, '2025-05-08', '2025-05-10', 'BUY', 20.000000, 95.5000, 1910.0000, 1914.0000, 'AUD', 'First ETF buy'),
(10, 5, '2025-05-15', '2025-05-15', 'DEPOSIT', 6000.000000, 1.0000, 6000.0000, 6000.0000, 'AUD', 'Income sleeve'),
(10, 3, '2025-05-22', '2025-05-24', 'BUY', 45.000000, 93.0000, 4185.0000, 4190.0000, 'AUD', 'Yield allocation'),
(10, 4, '2025-06-01', '2025-06-03', 'BUY', 25.000000, 56.2000, 1405.0000, 1410.0000, 'AUD', 'Global diversifier');
