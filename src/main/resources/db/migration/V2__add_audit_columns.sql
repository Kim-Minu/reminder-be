ALTER TABLE members
    ADD COLUMN created_by   VARCHAR(255),
    ADD COLUMN last_modified_by VARCHAR(255);

ALTER TABLE reminder_lists
    ADD COLUMN created_by   VARCHAR(255),
    ADD COLUMN last_modified_by VARCHAR(255);

ALTER TABLE reminders
    ADD COLUMN created_by   VARCHAR(255),
    ADD COLUMN last_modified_by VARCHAR(255);
