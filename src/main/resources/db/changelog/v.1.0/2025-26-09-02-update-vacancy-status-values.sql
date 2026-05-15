update vacancy_status set status = 'PUBLISHED' where status = 'OPENED';
insert into vacancy_status (status) values ('DRAFT');