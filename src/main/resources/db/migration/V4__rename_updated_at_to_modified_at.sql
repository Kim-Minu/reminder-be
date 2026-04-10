ALTER TABLE members
    RENAME COLUMN updated_at TO modified_at;

ALTER TABLE refresh_tokens
    RENAME COLUMN updated_at TO modified_at;

ALTER TABLE reminder_lists
    RENAME COLUMN updated_at TO modified_at;

ALTER TABLE reminders
    RENAME COLUMN updated_at TO modified_at;
