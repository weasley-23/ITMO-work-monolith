
create table if not exists roles(
    id bigserial primary key,
    name varchar(50) not null unique
);

insert into roles (name) values
     ('USER'),
     ('ADMIN'),
     ('EMPLOYER')
on conflict (name) do nothing;

create table if not exists users(
    id bigserial primary key,
    fullname varchar(255) not null,
    password varchar(255) not null,
    email varchar(320) not null unique,
    role_id bigint not null references roles(id) on delete RESTRICT
);


create table if not exists companies(
    id bigserial primary key,
    name varchar(320) not null,
    email varchar(320) not null unique,
    description text
);

create table if not exists user_companies(
    id bigserial primary key,
    user_id bigint not null references users(id) on delete cascade,
    company_id bigint not null references companies(id) on delete cascade
);


create table if not exists currencies(
    id bigserial primary key,
    currency varchar(3) not null unique
);

insert into currencies (currency) values
    ('RUB'),
    ('USD'),
    ('EUR')
on conflict (currency) do nothing;

create table if not exists vacancy_status(
    id bigserial primary key,
    status varchar(50) not null unique
);

insert into vacancy_status (status) values
    ('OPENED'),
    ('CLOSED')
on conflict (status) do nothing;

create table if not exists vacancies(
    id bigserial primary key,
    company_id bigint not null references companies(id) on delete cascade,
    title varchar(255) not null,
    description text,
    salary_from numeric(12, 0) check (salary_from >= 0),
    salary_to numeric(12, 0) check (salary_to >= 0),
    currency_id bigint not null references currencies(id),
    created_at timestamp not null default NOW(),
    status_id bigint not null references vacancy_status(id),
    check (salary_from is NULL or salary_to is NULL or salary_from <= salary_to)
);

create table if not exists application_status(
 id bigserial primary key,
 status varchar(50) not null unique
);

insert into application_status (status) values
    ('NEW'),
    ('VIEWED'),
    ('REJECTED'),
    ('ACCEPTED')
on conflict (status) do nothing;

create table if not exists applications(
   id bigserial primary key,
   user_id bigint not null references users(id) on delete cascade,
   vacancy_id bigint not null references vacancies(id) on delete cascade,
   cover_letter text,
   status_id bigint not null references application_status(id),
   created_at timestamp not null default NOW(),
   updated_at timestamp not null default NOW()

);