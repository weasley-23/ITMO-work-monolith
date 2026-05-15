insert into roles (name) values
    ('ROLE_COMPANY_OWNER')
on conflict (name) do nothing;