-- Message type enum (e.g. NEWS, ALERT, NOTICE)
CREATE TYPE message_type_enum AS ENUM ('NEWS', 'ALERT', 'NOTICE');

-- Message status enum (e.g. DRAFT, PUBLISHED, ARCHIVED, RETRACTED)
CREATE TYPE message_status_enum AS ENUM ('DRAFT', 'PUBLISHED', 'ARCHIVED', 'RETRACTED');

-- Messages table (created_date for unique constraint and ON CONFLICT)
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    msg_id VARCHAR(255) NOT NULL UNIQUE,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_date DATE GENERATED ALWAYS AS (created_at::date) STORED,
    message_type message_type_enum NOT NULL,
    issue BIGINT NOT NULL,
    status message_status_enum NOT NULL,
    effective_start DATE NOT NULL,
    effective_end DATE,
    created_by BIGINT,
    updated_by BIGINT,
    updated_at TIMESTAMP,
    CONSTRAINT fk_message_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_message_updated_by FOREIGN KEY (updated_by) REFERENCES users(id),
    CONSTRAINT uk_message_date_issue_type UNIQUE (created_date, issue, message_type)
);

CREATE INDEX idx_messages_msg_id ON messages(msg_id);
CREATE INDEX idx_messages_created_at_issue_type ON messages(DATE(created_at), issue, message_type);
CREATE INDEX idx_messages_effective_start ON messages(effective_start);
CREATE INDEX idx_messages_effective_end ON messages(effective_end);
CREATE INDEX idx_messages_created_by ON messages(created_by);
CREATE INDEX idx_messages_updated_by ON messages(updated_by);
