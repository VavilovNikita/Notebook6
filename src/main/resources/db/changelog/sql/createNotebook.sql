CREATE TABLE notebook
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    user_id    int REFERENCES users (id) ON DELETE CASCADE,
    title      varchar(255) NOT NULL,
    text       text         NOT NULL,
    created_at date NOT NULL,
    updated_at date NOT NULL
);



