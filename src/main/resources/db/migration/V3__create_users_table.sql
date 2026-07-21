CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY ,
    full_name TEXT NOT NULL ,
    email TEXT NOT NULL UNIQUE ,
    password TEXT NOT NULL ,
    role TEXT NOT NULL ,
    created_at TIMESTAMP NOT NULL
)