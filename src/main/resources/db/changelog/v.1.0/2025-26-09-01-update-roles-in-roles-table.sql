update roles set name = 'ROLE_USER' where name = 'USER';
update roles set name = 'ROLE_ADMIN' where name = 'ADMIN';
update roles set name = 'ROLE_MANAGER' where name = 'EMPLOYER';

insert into roles (name) values ('ROLE_EMPLOYEE') on conflict (name) do nothing;