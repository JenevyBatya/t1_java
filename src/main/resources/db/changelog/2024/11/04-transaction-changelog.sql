-- liquibase formatted sql

-- changeset e_cha:20241104-transaction-seq
CREATE SEQUENCE IF NOT EXISTS transaction_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:20241104-transaction-table
CREATE TABLE transaction
(
    id               BIGINT NOT NULL DEFAULT NEXTVAL('transaction_seq'),
    account_id       BIGINT,
    amount           DECIMAL(15, 2),
    time TIMESTAMP,
    status VARCHAR(255),
    CONSTRAINT pk_transaction PRIMARY KEY (id),
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE
);
