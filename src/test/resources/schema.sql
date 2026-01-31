CREATE TABLE users(
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      full_name VARCHAR(100) NOT NULL,
                      balance DECIMAL(15,2) NOT NULL CHECK (balance >= 0)
);

CREATE TABLE payments(
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         amount DECIMAL(15,2) NOT NULL,
                         status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id)
);