CREATE DATABASE socks_db;

CREATE TABLE Sock (
                      id BIGSERIAL PRIMARY KEY,
                      color VARCHAR(50) NOT NULL,
                      cottonPart INT NOT NULL CHECK (cottonPart >= 0 AND cottonPart <= 100),
                      quantity INT NOT NULL CHECK (quantity >= 0)
);

CREATE INDEX idx_sock_color ON Sock (color);
CREATE INDEX idx_sock_cottonPart ON Sock (cottonPart);
