create table company_status(
    id bigserial primary key,
    status varchar(50) not null unique
);

insert into company_status (status) values
    ('PENDING_VERIFICATION'),
    ('APPROVED'),
    ('REJECTED')
on conflict (status) do nothing;

alter table companies
    add column status_id bigint references company_status(id);