-- liquibase formatted sql

-- changeset e_cha:20241104-datasourceerrorlog-seq
CREATE SEQUENCE IF NOT EXISTS data_source_error_log_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:20241104-datasourceerrorlog-table
CREATE TABLE data_source_error_log
(
    id               BIGINT NOT NULL DEFAULT NEXTVAL('data_source_error_log_seq'),
    stack_trace      TEXT,
    message          TEXT,
    method_signature TEXT,
    CONSTRAINT pk_datasource_error_log PRIMARY KEY (id)
);
