--liquibase formatted sql

--changeset alvi:001-create-tables

CREATE SEQUENCE users_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE category_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE tasks_seq
    START WITH 1
    INCREMENT BY 1;


CREATE TABLE users (
                       id BIGINT NOT NULL DEFAULT nextval('users_seq'),
                       username VARCHAR(255),
                       email VARCHAR(255),
                       PRIMARY KEY (id)
);


CREATE TABLE category (
                          id BIGINT NOT NULL DEFAULT nextval('category_seq'),
                          name VARCHAR(255),
                          PRIMARY KEY (id)
);


CREATE TABLE tasks (
                       id BIGINT NOT NULL DEFAULT nextval('tasks_seq'),
                       title VARCHAR(255),
                       description VARCHAR(255),
                       task_status VARCHAR(255),
                       created_at TIMESTAMP,
                       user_id BIGINT,
                       category_id BIGINT,

                       PRIMARY KEY (id),

                       CONSTRAINT fk_tasks_user
                           FOREIGN KEY (user_id)
                               REFERENCES users(id),

                       CONSTRAINT fk_tasks_category
                           FOREIGN KEY (category_id)
                               REFERENCES category(id)
);