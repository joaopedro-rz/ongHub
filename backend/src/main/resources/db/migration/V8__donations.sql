CREATE TABLE donations (
    id BIGSERIAL PRIMARY KEY,
    donation_type VARCHAR(20) NOT NULL,
    donor_user_id BIGINT NOT NULL,
    campaign_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    amount NUMERIC(14, 2),
    payment_method VARCHAR(60),
    proof_url VARCHAR(512),
    material_description VARCHAR(500),
    quantity INT,
    campaign_item_id BIGINT,
    notes TEXT,
    confirmed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT fk_donations_donor FOREIGN KEY (donor_user_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_donations_campaign FOREIGN KEY (campaign_id) REFERENCES campaigns (id) ON DELETE RESTRICT,
    CONSTRAINT fk_donations_campaign_item FOREIGN KEY (campaign_item_id) REFERENCES campaign_items (id) ON DELETE SET NULL
);

CREATE INDEX idx_donations_donor_user_id ON donations (donor_user_id);
CREATE INDEX idx_donations_campaign_id ON donations (campaign_id);
CREATE INDEX idx_donations_status ON donations (status);

-- Subtipo (para atender ao modelo do TCC): tabelas especializadas por tipo
CREATE TABLE financial_donations (
    donation_id BIGINT PRIMARY KEY,
    amount NUMERIC(14, 2),
    payment_method VARCHAR(60),
    proof_url VARCHAR(512),
    notes TEXT,
    CONSTRAINT fk_fin_donation FOREIGN KEY (donation_id) REFERENCES donations (id) ON DELETE CASCADE
);

CREATE INDEX idx_financial_donations_amount ON financial_donations (amount);

CREATE TABLE material_donations (
    donation_id BIGINT PRIMARY KEY,
    material_description VARCHAR(500),
    quantity INT,
    campaign_item_id BIGINT,
    proof_url VARCHAR(512),
    notes TEXT,
    CONSTRAINT fk_mat_donation FOREIGN KEY (donation_id) REFERENCES donations (id) ON DELETE CASCADE,
    CONSTRAINT fk_mat_campaign_item FOREIGN KEY (campaign_item_id) REFERENCES campaign_items (id) ON DELETE SET NULL
);

CREATE INDEX idx_material_donations_campaign_item ON material_donations (campaign_item_id);

CREATE TABLE donation_receipts (
    id BIGSERIAL PRIMARY KEY,
    donation_id BIGINT NOT NULL UNIQUE,
    receipt_number VARCHAR(40) NOT NULL UNIQUE,
    pdf_path VARCHAR(512),
    issued_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_receipt_donation FOREIGN KEY (donation_id) REFERENCES donations (id) ON DELETE CASCADE
);
