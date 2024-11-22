-- liquibase formatted sql

-- changeset e_cha:20241104-account-seq
CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:20241104-account-table
CREATE TABLE account
(
    id         BIGINT NOT NULL DEFAULT NEXTVAL('account_seq'),
    client_id BIGINT,
    type VARCHAR(255),
    balance    DECIMAL(15, 2),
    frozen_amount    DECIMAL(15, 2),
    status VARCHAR(255),
    CONSTRAINT pk_account PRIMARY KEY (id)
);
