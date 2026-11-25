INSERT INTO clients (id, name, domain, logo_url, primary_color) VALUES
                                                                    (1, 'Loja BR', 'cliente1.local', 'https://placehold.co/200x60/FF5722/FFFFFF?text=Loja+BR', '#FF5722'),
                                                                    (2, 'Euro Store', 'cliente2.local', 'https://placehold.co/200x60/2196F3/FFFFFF?text=Euro+Store', '#2196F3'),
                                                                    (3, 'Multi Store', 'cliente3.local', 'https://placehold.co/200x60/4CAF50/FFFFFF?text=Multi+Store', '#4CAF50');

SELECT setval('clients_id_seq', (SELECT MAX(id) FROM clients));

INSERT INTO provider_configs (client_id, provider_name, is_active, priority) VALUES
                                                                                 (1, 'BRAZILIAN', TRUE, 1),
                                                                                 (2, 'EUROPEAN', TRUE, 1),
                                                                                 (3, 'BRAZILIAN', TRUE, 1),
                                                                                 (3, 'EUROPEAN', TRUE, 2);

-- Senha de todos os usuários de teste: password123
INSERT INTO users (client_id, name, email, password) VALUES
                                                         (1, 'Usuário Loja BR', 'user@cliente1.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a'),
                                                         (2, 'Usuário Euro Store', 'user@cliente2.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a'),
                                                         (3, 'Usuário Multi Store', 'user@cliente3.local', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a');