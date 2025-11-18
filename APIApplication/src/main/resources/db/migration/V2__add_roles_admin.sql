insert into roles(name)
values ('STUDENT'),
       ('TEACHER'),
       ('ADMIN');

insert into users(email, password, is_approved)
values ('amerscan8+admin@gmail.com', '$2a$10$YVgDrcNT5PDQkr.iAkF0pe.sApBLm46Mj592tTXJusR/0/qi3EdF2', true);

insert into user_roles(role_id, user_id)
values ((select id from roles where name = 'ADMIN'),
        (select id from users where email = 'amerscan8+admin@gmail.com'));

insert into user_info(id, first_name, last_name)
values ((select id from users where email = 'amerscan8+admin@gmail.com'),
        'Admin', 'Admin')