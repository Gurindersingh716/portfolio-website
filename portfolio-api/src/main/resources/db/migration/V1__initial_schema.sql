-- Initial schema for the portfolio API.

CREATE TABLE projects (
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR(160) NOT NULL,
    role          VARCHAR(80),
    status        VARCHAR(40)  NOT NULL,
    problem       TEXT,
    approach      TEXT,
    engineering   TEXT,
    source_url    VARCHAR(300),
    display_order INTEGER      NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE project_stack (
    project_id BIGINT      NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    tech       VARCHAR(60) NOT NULL
);

CREATE INDEX idx_project_stack_project ON project_stack (project_id);
CREATE INDEX idx_projects_display_order ON projects (display_order);

CREATE TABLE contact_messages (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(120) NOT NULL,
    email          VARCHAR(254) NOT NULL,
    subject        VARCHAR(160),
    message        TEXT         NOT NULL,
    source_ip_hash VARCHAR(64),
    received_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_contact_received_at ON contact_messages (received_at DESC);
