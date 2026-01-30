-- Quick Setup Script for PostgreSQL
-- Connect to PostgreSQL as superuser:

-- Create databases
CREATE DATABASE payment_db_dev;
CREATE DATABASE payment_db_prod;
CREATE DATABASE payment_db;

-- Create users
CREATE USER dev_user WITH PASSWORD 'dev_password';
CREATE USER prod_user WITH PASSWORD 'prod_password';

-- Grant database permissions
GRANT ALL PRIVILEGES ON DATABASE payment_db_dev TO dev_user;
GRANT CONNECT ON DATABASE payment_db_prod TO prod_user;

-- Connect to dev database and grant table permissions
\c payment_db_dev
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO dev_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO dev_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO dev_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO dev_user;

-- Connect to prod database and grant table permissions
\c payment_db_prod
GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA public TO prod_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO prod_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE ON TABLES TO prod_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO prod_user;



--Create Tables in Each Database
--For payment_db_dev:
-- psql -U postgres -d payment_db_dev
CREATE TABLE users(
                      id BIGSERIAL PRIMARY KEY,
                      full_name VARCHAR(100) NOT NULL,
                      balance DECIMAL(15,2) NOT NULL CHECK (balance >= 0)
);

CREATE TABLE payments(
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         amount DECIMAL(15,2) NOT NULL,
                         status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO users (full_name, balance) VALUES
                                           ('Dev User 1', 1000.00),
                                           ('Dev User 2', 500.00),
                                           ('Dev User 3', 250.00);



-- For payment_db_prod:
-- psql -U postgres -d payment_db_prod
CREATE TABLE users(
                      id BIGSERIAL PRIMARY KEY,
                      full_name VARCHAR(100) NOT NULL,
                      balance DECIMAL(15,2) NOT NULL CHECK (balance >= 0)
);

CREATE TABLE payments(
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         amount DECIMAL(15,2) NOT NULL,
                         status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id)
);


-- For payment_db (default):
-- psql -U postgres -d payment_db

CREATE TABLE users(
                      id BIGSERIAL PRIMARY KEY,
                      full_name VARCHAR(100) NOT NULL,
                      balance DECIMAL(15,2) NOT NULL CHECK (balance >= 0)
);

CREATE TABLE payments(
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         amount DECIMAL(15,2) NOT NULL,
                         status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO users (full_name, balance) VALUES ('Test User', 500.00);