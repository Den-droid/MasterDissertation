insert into urls(url, description, method)
values ('/api/universities', 'Додати університет', 1),
       ('/api/universities', 'Оновити університет', 2),
       ('/api/universities', 'Видалити університет', 3),
       ('/api/subjects', 'Додати тему', 1),
       ('/api/subjects', 'Оновити тему', 2),
       ('/api/subjects', 'Видалити тему', 3);

insert into url_fields(url_id, field_id, required)
values ((select id from urls where url = '/api/universities' and method = 1),
        (select id from fields where name = 'name'),
        true);

insert into url_fields(url_id, field_id, required)
values ((select id from urls where url = '/api/universities' and method = 2),
        (select id from fields where name = 'id'),
        true),
       ((select id from urls where url = '/api/universities' and method = 2),
        (select id from fields where name = 'name'),
        true);

insert into url_fields(url_id, field_id, required)
values ((select id from urls where url = '/api/universities' and method = 3),
        (select id from fields where name = 'universityId'),
        true);

insert into url_fields(url_id, field_id, required)
values ((select id from urls where url = '/api/subjects' and method = 1),
        (select id from fields where name = 'name'),
        true),
       ((select id from urls where url = '/api/subjects' and method = 1),
        (select id from fields where name = 'universityId'),
        true);

insert into url_fields(url_id, field_id, required)
values ((select id from urls where url = '/api/subjects' and method = 2),
        (select id from fields where name = 'id'),
        true),
       ((select id from urls where url = '/api/subjects' and method = 2),
        (select id from fields where name = 'name'),
        true),
       ((select id from urls where url = '/api/subjects' and method = 2),
        (select id from fields where name = 'universityId'),
        false);

insert into url_fields(url_id, field_id, required)
values ((select id from urls where url = '/api/subjects' and method = 3),
        (select id from fields where name = 'subjectId'),
        true);