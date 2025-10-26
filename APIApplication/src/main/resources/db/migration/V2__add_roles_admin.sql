insert into roles(name)
values ('STUDENT'), ('TEACHER'), ('ADMIN');

insert into users(email, password)
values('amerscan8+admin@gmail.com', '$2a$10$YVgDrcNT5PDQkr.iAkF0pe.sApBLm46Mj592tTXJusR/0/qi3EdF2');

insert into user_info(id, first_name, last_name)
values ((select id from users where email = 'amerscan8+admin@gmail.com'),
        'Admin', 'Admin')