CREATE TABLE users
(
    id            BIGSERIAL    NOT NULL PRIMARY KEY,
    role_id       int REFERENCES role (id) ON DELETE CASCADE,
    user_name     varchar(255) NOT NULL,
    password      varchar(255) NOT NULL,
    first_name    varchar(255) NOT NULL,
    last_name     varchar(255) NOT NULL,
    email         varchar(255) NOT NULL,
    date_of_birth date NOT NULL
);

INSERT INTO users(role_id, user_name, password, first_name, last_name, email, date_of_birth)
VALUES (2, 'root', '$2a$10$mPy4.EttH3ld6fNSZK1A0eMznADiBm2y4qb/pImTmS214f3.SqgFO', 'админ', 'админ', 'admin@mail.ru', '2023-08-15');




