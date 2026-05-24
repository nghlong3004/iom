CREATE TABLE external_accounts (
    id               BIGSERIAL    PRIMARY KEY,
    user_id          BIGINT       NOT NULL REFERENCES app_users(id),
    platform         VARCHAR(20)  NOT NULL,
    external_user_id VARCHAR(100) NOT NULL,
    display_name     VARCHAR(100),
    linked_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (platform, external_user_id)
);

CREATE INDEX idx_ext_account_lookup ON external_accounts(platform, external_user_id);
