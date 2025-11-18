insert into universities(name)
values ('Київський політехнічний інститут');

insert into subjects(name, university_id)
values ('Методи оптимізації функцій',
        (select id from universities where name = 'Київський політехнічний інститут'));

insert into functions(text, variables_count, subject_id)
values ('x1^2', 1,
        (select id from subjects where name = 'Методи оптимізації функцій')),
       ('-(x1^2)+2', 1,
        (select id from subjects where name = 'Методи оптимізації функцій'));

insert into function_min_max_values(value, function_result_type, function_id)
values (0, 0, (select id from functions where text = 'x1^2')),
       (2, 1, (select id from functions where text = '-(x1^2)+2'));

insert into user_permissions(user_id, university_id)
values ((select id from users where email = 'amerscan8+admin@gmail.com'),
        (select id from universities where name = 'Київський політехнічний інститут'));