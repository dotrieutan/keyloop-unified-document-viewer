CREATE TABLE document_search_audit (
    id UUID PRIMARY KEY,
    correlation_id UUID NOT NULL UNIQUE,
    vin_fingerprint CHAR(64) NOT NULL,
    requested_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ NOT NULL,
    outcome VARCHAR(16) NOT NULL,
    sales_outcome VARCHAR(24) NOT NULL,
    service_outcome VARCHAR(24) NOT NULL,
    result_count INTEGER NOT NULL CHECK (result_count >= 0),
    duration_ms BIGINT NOT NULL CHECK (duration_ms >= 0)
);

CREATE INDEX document_search_audit_requested_at_idx
    ON document_search_audit (requested_at DESC);

CREATE INDEX document_search_audit_vin_fingerprint_idx
    ON document_search_audit (vin_fingerprint);

