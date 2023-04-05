-- INSERT INTO ROLE (name) VALUES ('USER');
-- INSERT INTO ROLE (name) VALUES ('ADMIN');
INSERT INTO users(first_name, last_name, phone_number, email, role, password, password_last_changed, is_activated) VALUES ('Zika', 'Peric', '+381698566565', 'zika@peric.com', 'ADMIN', '$2a$12$G/IJUGIXL9ohQeMMpXwuM.wpRKKDYKOWqS9LK/ra2jlq0.QSt4j6a', '2023-04-04 22:58:40.985741', true);
INSERT INTO users(first_name, last_name, phone_number, email, role, password, password_last_changed, is_activated) VALUES ('Mika', 'Peric', '+381618566565', 'mika@peric.com', 'ADMIN', '$2a$12$G/IJUGIXL9ohQeMMpXwuM.wpRKKDYKOWqS9LK/ra2jlq0.QSt4j6a', '2023-04-04 22:58:40.985741', true);

-- insert into users (email, first_name, is_activated, last_name, password, password_last_changed, phone_number, role)
-- values ('Zika', 'Peric', '+381698566565', 'zika@peric.com', 'ADMIN', '$2a$12$G/IJUGIXL9ohQeMMpXwuM.wpRKKDYKOWqS9LK/ra2jlq0.QSt4j6a', '2023-04-04 22:58:40.985741', true);