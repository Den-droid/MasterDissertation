insert into urls(url, description, method)
values ('/api/functions', 'Додати функцію', 1),
       ('/api/functions', 'Оновити функцію', 2),
       ('/api/functions', 'Видалити функцію', 3),
       ('/api/assignmentRestrictions/defaultRestrictions',
        'Отримати обмеження для завдань по замовчуванню', 0),
       ('/api/permissions', 'Отримати права користувача', 0),
       ('/api/users', 'Отримати дані про користувачів', 0);

insert into fields(name, label, description, type)
values ('minValues', 'Мінімальні значення функції', 'Мінімальні значення функції у форматі' ||
                                                    'x1=1;x2=2;...', 2),
       ('maxValues', 'Максимальні значення функції', 'Максимальні значення функції у форматі' ||
                                                     'x1=1;x2=2;...', 2),
       ('text', 'Текст функції', 'Текст функції у форматі' ||
                                 'x1^2+x2^3...', 2),
       ('variablesCount', 'Кількість змінних у функції', 'Кількість змінних у функції', 0);

insert into url_fields(url_id, field_id, required)
values ((select id
         from urls
         where url = '/api/functions'
           and method = 1), (select id from fields where name = 'text'),
        true)
        ,
       ((select id
         from urls
         where url = '/api/functions'
           and method = 1), (select id from fields where name = 'variablesCount'),
        true)
        ,
       ((select id
         from urls
         where url = '/api/functions'
           and method = 1), (select id from fields where name = 'minValues'),
        true)
        ,
       ((select id
         from urls
         where url = '/api/functions'
           and method = 1), (select id from fields where name = 'maxValues'),
        true)
        ,
       ((select id
         from urls
         where url = '/api/functions'
           and method = 1), (select id from fields where name = 'subjectId'),
        false);

insert into url_fields(url_id, field_id, required)
values ((select id from urls where url = '/api/functions' and method = 2),
        (select id from fields where name = 'id'),
        true),
       ((select id
         from urls
         where url = '/api/functions'
           and method = 2), (select id from fields where name = 'text'),
        true)
        ,
       ((select id
         from urls
         where url = '/api/functions'
           and method = 2), (select id from fields where name = 'variablesCount'),
        true)
        ,
       ((select id
         from urls
         where url = '/api/functions'
           and method = 2), (select id from fields where name = 'minValues'),
        true)
        ,
       ((select id
         from urls
         where url = '/api/functions'
           and method = 2), (select id from fields where name = 'maxValues'),
        true)
        ,
       ((select id
         from urls
         where url = '/api/functions'
           and method = 2), (select id from fields where name = 'subjectId'),
        false);

insert into url_fields(url_id, field_id, required)
values ((select id from urls where url = '/api/functions' and method = 3),
        (select id from fields where name = 'functionId'),
        true);

insert into url_fields(url_id, field_id, required)
values ((select id
         from urls
         where url = '/api/assignmentRestrictions/defaultRestrictions'),
        (select id from fields where name = 'functionId'),
        false),
       ((select id
         from urls
         where url = '/api/assignmentRestrictions/defaultRestrictions'),
        (select id from fields where name = 'subjectId'),
        false),
       ((select id
         from urls
         where url = '/api/assignmentRestrictions/defaultRestrictions'),
        (select id from fields where name = 'universityId'),
        false);

insert into url_fields(url_id, field_id, required)
values ((select id
         from urls
         where url = '/api/permissions'),
        (select id from fields where name = 'userId'),
        false);

insert into url_fields(url_id, field_id, required)
values ((select id
         from urls
         where url = '/api/permissions'),
        (select id from fields where name = 'userId'),
        false);