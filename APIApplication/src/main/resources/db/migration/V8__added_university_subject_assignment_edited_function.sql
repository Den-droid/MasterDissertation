insert into universities(name)
values ('Кихвський політехнічний університет');

insert into subjects(name, university_id)
values ('Методи оптимізації функцій', 1);

insert into assignments(function_result_type, text)
values (0, 'Знайдіть мінімальне значення функції');

insert into assignments(function_result_type, text)
values (1, 'Знайдіть максимальне значення функції');

insert into default_assignment_restrictions(university_id, assignment_restriction_type, attempts_remaining,
                                            minutes_for_attempt, minutes_to_deadline)
values (1, 0, 15, 0, 0);

update functions
set min_values = '0', subject_id = 1
where id = 1;
