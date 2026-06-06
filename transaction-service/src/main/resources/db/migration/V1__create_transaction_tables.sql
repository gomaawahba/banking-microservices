CREATE TABLE transactions (
    id VARCHAR(36) PRIMARY KEY,

    reference_number VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    from_account_id VARCHAR(36),
    to_account_id VARCHAR(36),

    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'SAR',
    fee DECIMAL(10,2) NOT NULL DEFAULT 0,

    description VARCHAR(500),

    initiated_by VARCHAR(36) NOT NULL,

    failure_reason VARCHAR(255),
    external_ref VARCHAR(100),

    processed_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_tx_from_account ON transactions(from_account_id);
CREATE INDEX idx_tx_to_account ON transactions(to_account_id);
CREATE INDEX idx_tx_user ON transactions(initiated_by);
CREATE INDEX idx_tx_status ON transactions(status);
CREATE INDEX idx_tx_created ON transactions(created_at);
CREATE INDEX idx_tx_reference ON transactions(reference_number);

-- optional metadata
ALTER TABLE transactions
COMMENT = 'جدول العمليات المالية - التحويلات والإيداعات والسحوبات';