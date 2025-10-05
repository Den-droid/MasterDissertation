update urls
set method = 2
where url = '/api/assignmentRestrictions/setDefaultRestriction';

insert into urls(url, description, method)
values ('/api/permissions/removePermission', 'Забрати доступ на ресурс', 3),
    ('/api/assignmentRestrictions/deleteDefaultRestriction', 'Видалити обмеження для завдання по замовчуванню', 3);