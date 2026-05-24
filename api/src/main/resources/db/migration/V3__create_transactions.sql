CREATE TABLE transactions (
    id              BIGSERIAL    PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES app_users(id),
    type            VARCHAR(10)  NOT NULL,
    amount          BIGINT       NOT NULL,
    currency        VARCHAR(5)   NOT NULL DEFAULT 'VND',
    category        VARCHAR(30)  NOT NULL DEFAULT 'OTHER',
    note            VARCHAR(500),
    raw_input       TEXT,
    source_platform VARCHAR(20)  NOT NULL,
    occurred_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tx_user_occurred ON transactions(user_id, occurred_at);
