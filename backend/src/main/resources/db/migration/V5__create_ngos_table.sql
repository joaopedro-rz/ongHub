CREATE TABLE ngos (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    cnpj VARCHAR(20) UNIQUE,
    description TEXT,
    phone VARCHAR(30),
    website VARCHAR(255),
    email VARCHAR(255),

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    manager_user_id BIGINT NOT NULL,
    category_id BIGINT,
    address_id BIGINT,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,

    CONSTRAINT fk_ngos_manager_user
        FOREIGN KEY (manager_user_id) REFERENCES users (id) ON DELETE RESTRICT,

    CONSTRAINT fk_ngos_category
        FOREIGN KEY (category_id) REFERENCES ngo_categories (id) ON DELETE SET NULL,

    CONSTRAINT fk_ngos_address
        FOREIGN KEY (address_id) REFERENCES addresses (id) ON DELETE SET NULL
);

CREATE INDEX idx_ngos_status ON ngos (status);
CREATE INDEX idx_ngos_manager_user_id ON ngos (manager_user_id);
CREATE INDEX idx_ngos_category_id ON ngos (category_id);
