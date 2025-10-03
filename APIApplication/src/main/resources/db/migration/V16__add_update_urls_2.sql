update urls
set url = '/api/assignments/{userAssignmentId}/marks'
where id = 23;

update urls
set url = '/api/assignments/restrictionTypes'
where id = 24;

update urls
set description = 'Отримати список предметів'
where id = 18;

insert into url_fields(url_id, field_id, required)
values (18, (select id from fields where name = 'universityId'), false);

update urls
set description = 'Отримати список API запитів'
where id = 20;

insert into urls(url, description, method)
values ('/api/functions', 'Отримати список функцій', 0);

insert into fields(name, label, description, type)
values ('subjectId', 'Ідентифікатор', 'Унікальний ID предмету', 0);

insert into url_fields(url_id, field_id, required)
values ((select id from urls where url = '/api/functions'),
        (select id from fields where name = 'subjectId'),
        false);