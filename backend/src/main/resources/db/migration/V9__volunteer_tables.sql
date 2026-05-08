CREATE TABLE volunteer_opportunities (
    id BIGSERIAL PRIMARY KEY,
    ngo_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    skills_required TEXT,
    slots_available INT NOT NULL DEFAULT 1,
    hours_per_week INT,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT fk_volunteer_opps_ngo FOREIGN KEY (ngo_id) REFERENCES ngos (id) ON DELETE CASCADE
);

CREATE INDEX idx_volunteer_opps_ngo_id ON volunteer_opportunities (ngo_id);

CREATE TABLE volunteer_applications (
    id BIGSERIAL PRIMARY KEY,
    opportunity_id BIGINT NOT NULL,
    volunteer_user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    skills_note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT fk_volunteer_apps_opp FOREIGN KEY (opportunity_id) REFERENCES volunteer_opportunities (id) ON DELETE CASCADE,
    CONSTRAINT fk_volunteer_apps_user FOREIGN KEY (volunteer_user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_volunteer_apps_opp_user UNIQUE (opportunity_id, volunteer_user_id)
);

CREATE INDEX idx_volunteer_apps_opp_id ON volunteer_applications (opportunity_id);

CREATE TABLE volunteer_schedules (
    id BIGSERIAL PRIMARY KEY,
    opportunity_id BIGINT NOT NULL,
    volunteer_user_id BIGINT,
    slot_start TIMESTAMP NOT NULL,
    slot_end TIMESTAMP NOT NULL,
    title VARCHAR(255),
    CONSTRAINT fk_volunteer_sched_opp FOREIGN KEY (opportunity_id) REFERENCES volunteer_opportunities (id) ON DELETE CASCADE,
    CONSTRAINT fk_volunteer_sched_user FOREIGN KEY (volunteer_user_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE INDEX idx_volunteer_sched_opp_id ON volunteer_schedules (opportunity_id);
