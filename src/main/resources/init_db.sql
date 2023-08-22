INSERT INTO companies (name, code)
VALUES ('Company A', 'A'),
       ('Company B', 'B'),
       ('Company C', 'C'),
       ('Company Z', 'Z');

INSERT INTO quotes (id, company_id, created_at, price)
VALUES
    (1, 1, '2023-08-17 10:00:00', 100.00),
    (2, 1, '2023-08-17 10:15:00', 105.50),
    -- и так далее
    (1000, 1000, '2023-08-17 16:45:00', 500.75);