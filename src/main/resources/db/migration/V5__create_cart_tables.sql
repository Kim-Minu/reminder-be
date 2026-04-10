CREATE TABLE cart_weeks
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    member_id     BIGINT       NOT NULL,
    cart_year     INT          NOT NULL,
    cart_month    INT          NOT NULL,
    week_of_month INT          NOT NULL,
    label         VARCHAR(50)  NOT NULL,
    created_at    DATETIME     NOT NULL,
    modified_at   DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_cart_weeks (member_id, cart_year, cart_month, week_of_month),
    CONSTRAINT fk_cart_weeks_member FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE
);

CREATE TABLE cart_items
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    cart_week_id  BIGINT       NOT NULL,
    name          VARCHAR(255) NOT NULL,
    quantity      INT          NOT NULL DEFAULT 1,
    unit_price    INT          NOT NULL DEFAULT 0,
    is_checked    BOOLEAN      NOT NULL DEFAULT FALSE,
    display_order INT          NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL,
    modified_at   DATETIME     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_cart_items_week FOREIGN KEY (cart_week_id) REFERENCES cart_weeks (id) ON DELETE CASCADE
);
