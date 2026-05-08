CREATE TABLE campaigns (
    id BIGSERIAL PRIMARY KEY,
    ngo_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    financial_goal NUMERIC(14, 2),
    start_date DATE,
    end_date DATE,
    cover_image_url VARCHAR(512),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    urgent BOOLEAN NOT NULL DEFAULT FALSE,
    category VARCHAR(120),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT fk_campaigns_ngo FOREIGN KEY (ngo_id) REFERENCES ngos (id) ON DELETE CASCADE
);

CREATE INDEX idx_campaigns_ngo_id ON campaigns (ngo_id);
CREATE INDEX idx_campaigns_status ON campaigns (status);

CREATE TABLE campaign_items (
    id BIGSERIAL PRIMARY KEY,
    campaign_id BIGINT NOT NULL,
    item_name VARCHAR(200) NOT NULL,
    category VARCHAR(120),
    quantity_needed INT NOT NULL DEFAULT 0,
    quantity_received INT NOT NULL DEFAULT 0,
    unit VARCHAR(40),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT fk_campaign_items_campaign FOREIGN KEY (campaign_id) REFERENCES campaigns (id) ON DELETE CASCADE
);

CREATE INDEX idx_campaign_items_campaign_id ON campaign_items (campaign_id);

CREATE TABLE campaign_updates (
    id BIGSERIAL PRIMARY KEY,
    campaign_id BIGINT NOT NULL,
    title VARCHAR(255),
    body TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_campaign_updates_campaign FOREIGN KEY (campaign_id) REFERENCES campaigns (id) ON DELETE CASCADE
);

CREATE INDEX idx_campaign_updates_campaign_id ON campaign_updates (campaign_id);
