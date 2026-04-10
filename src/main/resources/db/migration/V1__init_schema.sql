CREATE TABLE members
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  NOT NULL DEFAULT 'USER',
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_members_email (email)
);

CREATE TABLE refresh_tokens
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    token      VARCHAR(255) NOT NULL,
    member_id  BIGINT       NOT NULL,
    expires_at DATETIME     NOT NULL,
    created_at DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_refresh_tokens_token (token),
    CONSTRAINT fk_refresh_tokens_member FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE
);

CREATE TABLE reminder_lists
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    member_id     BIGINT       NOT NULL,
    name          VARCHAR(255) NOT NULL,
    color         VARCHAR(20)  NOT NULL DEFAULT '#007AFF',
    display_order INT          NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL,
    updated_at    DATETIME     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_reminder_lists_member FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE
);

CREATE TABLE reminders
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    list_id       BIGINT       NOT NULL,
    title         VARCHAR(255) NOT NULL,
    is_completed  BOOLEAN      NOT NULL DEFAULT FALSE,
    display_order INT          NOT NULL DEFAULT 0,
    notes         TEXT,
    is_flagged    BOOLEAN      NOT NULL DEFAULT FALSE,
    priority      VARCHAR(20)  NOT NULL DEFAULT 'NONE',
    due_date      DATE,
    due_time      TIME,
    completed_at  DATETIME,
    created_at    DATETIME     NOT NULL,
    updated_at    DATETIME     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_reminders_list FOREIGN KEY (list_id) REFERENCES reminder_lists (id) ON DELETE CASCADE
);
