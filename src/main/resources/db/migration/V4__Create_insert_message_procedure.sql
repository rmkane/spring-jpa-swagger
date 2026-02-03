-- insert_message: upsert by (created_date, issue, message_type); returns id (new or existing)
CREATE OR REPLACE FUNCTION insert_message(
    p_subject VARCHAR(255),
    p_message TEXT,
    p_created_at TIMESTAMP,
    p_message_type message_type_enum,
    p_issue BIGINT,
    p_status message_status_enum,
    p_effective_start DATE,
    p_effective_end DATE,
    p_created_by BIGINT,
    p_updated_by BIGINT
)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
    v_msg_id VARCHAR(255);
    v_id BIGINT;
BEGIN
    INSERT INTO messages (msg_id, subject, message, created_at, message_type, issue, status, effective_start, effective_end, created_by, updated_by, updated_at)
    VALUES (
        TO_CHAR(p_created_at, 'YYYY-MM-DD') || '/' || p_message_type::text || '/' || p_issue,
        p_subject,
        p_message,
        p_created_at,
        p_message_type,
        p_issue,
        p_status,
        p_effective_start,
        p_effective_end,
        p_created_by,
        p_updated_by,
        CURRENT_TIMESTAMP
    )
    ON CONFLICT (created_date, issue, message_type)
    DO UPDATE SET
        subject = EXCLUDED.subject,
        message = EXCLUDED.message,
        status = EXCLUDED.status,
        effective_start = EXCLUDED.effective_start,
        effective_end = EXCLUDED.effective_end,
        updated_by = EXCLUDED.updated_by,
        updated_at = CURRENT_TIMESTAMP
    RETURNING id INTO v_id;

    RETURN v_id;
END;
$$;
