CREATE TABLE configuracao (
    id BIGSERIAL PRIMARY KEY,
    internet_ip_test VARCHAR(255),
    internet_timeout_ms INTEGER,
    internet_period_seconds INTEGER,
    disk_period_seconds INTEGER,
    disk_alert_threshold_percent INTEGER,
    camera_period_seconds INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE camera (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    ip VARCHAR(255) NOT NULL,
    http_port INTEGER NOT NULL,
    rtsp_port INTEGER NOT NULL,
    username VARCHAR(255),
    password_encrypted VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE monitoramento_internet (
    id BIGSERIAL PRIMARY KEY,
    executed_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    response_time_ms BIGINT,
    error_message VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE monitoramento_disco (
    id BIGSERIAL PRIMARY KEY,
    executed_at TIMESTAMP NOT NULL,
    total_bytes BIGINT NOT NULL,
    used_bytes BIGINT NOT NULL,
    free_bytes BIGINT NOT NULL,
    usage_percentage DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE monitoramento_camera (
    id BIGSERIAL PRIMARY KEY,
    camera_id BIGINT NOT NULL,
    executed_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    ping_success BOOLEAN NOT NULL,
    ping_response_time_ms BIGINT,
    frame_capture_success BOOLEAN NOT NULL,
    frame_capture_time_ms BIGINT,
    error_message VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE alerta (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    severity VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    camera_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP
);

CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE stream_session (
    id BIGSERIAL PRIMARY KEY,
    camera_id BIGINT NOT NULL,
    user_id BIGINT,
    status VARCHAR(255) NOT NULL,
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO configuracao (
    internet_ip_test,
    internet_timeout_ms,
    internet_period_seconds,
    disk_period_seconds,
    disk_alert_threshold_percent,
    camera_period_seconds
) VALUES (
    '8.8.8.8',
    5000,
    60,
    120,
    85,
    180
);

INSERT INTO usuario (username, password, role, enabled)
VALUES ('admin', '$2a$10$Y49qFna3Mj44nCIgwcQwAubuYb774TjYIvTnQmqxMfgznlWyh/Mrq', 'ADMIN', TRUE);
