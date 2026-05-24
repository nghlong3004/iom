CREATE TABLE app_users (
    id            BIGSERIAL    PRIMARY KEY,
    email         VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    first_name    VARCHAR(35),
    last_name     VARCHAR(20),
    avatar_url    TEXT,
    auth_provider VARCHAR(20)  NOT NULL DEFAULT 'LOCAL',
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER',
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
