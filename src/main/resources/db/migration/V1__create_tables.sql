CREATE TABLE clients (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         domain VARCHAR(150) NOT NULL UNIQUE,
                         logo_url VARCHAR(255) NOT NULL,
                         primary_color VARCHAR(7) NOT NULL,
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE provider_configs (
                                  id BIGSERIAL PRIMARY KEY,
                                  client_id BIGINT NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
                                  provider_name VARCHAR(50) NOT NULL,
                                  is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                  priority INT NOT NULL DEFAULT 1,
                                  CONSTRAINT uq_client_provider UNIQUE (client_id, provider_name)
);

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       client_id BIGINT NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
                       name VARCHAR(120) NOT NULL,
                       email VARCHAR(150) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT uq_client_user_email UNIQUE (client_id, email)
);

CREATE INDEX idx_clients_domain ON clients(domain);
CREATE INDEX idx_provider_configs_client ON provider_configs(client_id);
CREATE INDEX idx_users_client_email ON users(client_id, email);