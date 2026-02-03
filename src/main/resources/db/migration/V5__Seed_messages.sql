-- Seed messages (dozen sample rows)
INSERT INTO messages (msg_id, subject, message, created_at, message_type, issue, status, effective_start, effective_end, created_by, updated_by, updated_at)
VALUES
    ('2025-01-06/NEWS/1', 'Weekly digest', 'Summary of updates for the first week of January.', '2025-01-06 09:00:00', 'NEWS', 1, 'PUBLISHED', '2025-01-06', '2025-01-12', 1, 1, CURRENT_TIMESTAMP),
    ('2025-01-06/ALERT/1', 'Maintenance window', 'Scheduled maintenance on Sunday 02:00-04:00 UTC.', '2025-01-06 10:00:00', 'ALERT', 1, 'PUBLISHED', '2025-01-06', NULL, 1, NULL, NULL),
    ('2025-01-13/NEWS/2', 'Weekly digest', 'Summary of updates for the second week of January.', '2025-01-13 09:00:00', 'NEWS', 2, 'PUBLISHED', '2025-01-13', '2025-01-19', 1, 1, CURRENT_TIMESTAMP),
    ('2025-01-13/NOTICE/1', 'Policy update', 'Please review the updated terms of service.', '2025-01-13 11:00:00', 'NOTICE', 1, 'PUBLISHED', '2025-01-13', '2025-02-13', 1, NULL, NULL),
    ('2025-01-20/NEWS/3', 'Weekly digest', 'Summary of updates for the third week of January.', '2025-01-20 09:00:00', 'NEWS', 3, 'PUBLISHED', '2025-01-20', '2025-01-26', 1, 1, CURRENT_TIMESTAMP),
    ('2025-01-20/ALERT/2', 'Incident resolved', 'The earlier connectivity issue has been resolved.', '2025-01-20 14:30:00', 'ALERT', 2, 'ARCHIVED', '2025-01-20', '2025-01-21', 1, 1, CURRENT_TIMESTAMP),
    ('2025-01-27/NEWS/4', 'Weekly digest', 'Summary of updates for the fourth week of January.', '2025-01-27 09:00:00', 'NEWS', 4, 'PUBLISHED', '2025-01-27', '2025-02-02', 1, 1, CURRENT_TIMESTAMP),
    ('2025-01-27/NOTICE/2', 'Holiday hours', 'Support will be limited on Feb 1 for the holiday.', '2025-01-27 12:00:00', 'NOTICE', 2, 'PUBLISHED', '2025-01-27', '2025-02-01', 1, NULL, NULL),
    ('2025-02-03/NEWS/5', 'Weekly digest', 'Summary of updates for the first week of February.', '2025-02-03 09:00:00', 'NEWS', 5, 'PUBLISHED', '2025-02-03', '2025-02-09', 1, 1, CURRENT_TIMESTAMP),
    ('2025-02-03/ALERT/3', 'New feature release', 'Feature X is now available in production.', '2025-02-03 15:00:00', 'ALERT', 3, 'PUBLISHED', '2025-02-03', NULL, 1, 1, CURRENT_TIMESTAMP),
    ('2025-02-10/NEWS/6', 'Weekly digest', 'Summary of updates for the second week of February.', '2025-02-10 09:00:00', 'NEWS', 6, 'DRAFT', '2025-02-10', '2025-02-16', 1, NULL, NULL),
    ('2025-02-10/NOTICE/3', 'Survey reminder', 'Don''t forget to complete the quarterly survey by Friday.', '2025-02-10 10:00:00', 'NOTICE', 3, 'PUBLISHED', '2025-02-10', '2025-02-14', 1, 1, CURRENT_TIMESTAMP);
