CREATE TABLE IF NOT EXISTS quantity_measurement_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    this_value DOUBLE,
    this_unit_name VARCHAR(64),
    this_measurement_type VARCHAR(64),
    that_value DOUBLE,
    that_unit_name VARCHAR(64),
    that_measurement_type VARCHAR(64),
    operation VARCHAR(32) NOT NULL,
    result_value DOUBLE,
    result_unit_name VARCHAR(64),
    result_measurement_type VARCHAR(64),
    comparison_result BOOLEAN,
    division_result DOUBLE,
    is_error BOOLEAN NOT NULL,
    error_message VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_qm_operation ON quantity_measurement_entity(operation);
CREATE INDEX IF NOT EXISTS idx_qm_type ON quantity_measurement_entity(this_measurement_type);

CREATE TABLE IF NOT EXISTS quantity_measurement_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    measurement_id BIGINT,
    operation VARCHAR(32) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (measurement_id) REFERENCES quantity_measurement_entity(id)
);

