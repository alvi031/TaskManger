
--liquibase formatted sql

--changeset alvi:002-create-outbox-event

CREATE TABLE outbox_event (
                              event_id UUID PRIMARY KEY,
                              task_id BIGINT NOT NULL,
                              event_type VARCHAR(50) NOT NULL,
                              payload TEXT NOT NULL,
                              status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              sent_at TIMESTAMP,

                              CONSTRAINT chk_outbox_event_status
                                  CHECK (status IN ('PENDING', 'SENT'))
);

CREATE INDEX idx_outbox_event_status_created_at
    ON outbox_event (status, created_at);
