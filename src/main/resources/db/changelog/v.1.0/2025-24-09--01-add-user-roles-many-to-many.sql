--liquibase formatted sql

--changeset arslanefimov:1
alter table users drop column if exists role_id;

--changeset arslanefimov:2
create table user_roles (
    id bigserial primary key,
    user_id bigint not null references users(id) on delete cascade,
    role_id bigint not null references roles(id) on delete cascade
);