DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS budgets;

CREATE TABLE budgets
(
    id           BIGINT  NOT NULL AUTO_INCREMENT,
    member_id    BIGINT  NOT NULL,
    budget_year  INT     NOT NULL,
    budget_month INT     NULL,
    amount       INT     NOT NULL,
    created_at   DATETIME NOT NULL,
    modified_at  DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_budgets (member_id, budget_year, budget_month),
    CONSTRAINT fk_budgets_member FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE
);
