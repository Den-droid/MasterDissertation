update urls
set method = 2
where url = '/api/assignmentRestrictions/setDefaultRestriction';

insert into urls(url, description, method)
values ('/api/permissions/removePermission', 'Забрати доступ на ресурс', 3),
    ('/api/assignmentRestrictions/deleteDefaultRestriction', 'Видалити обмеження для завдання по замовчуванню', 3);

insert into url_fields(url_id, field_id, required)
values ((select id from urls where url = '/api/assignmentRestrictions/deleteDefaultRestriction'),
        (select id from fields where name = 'functionId'),
        false),
       ((select id from urls where url = '/api/assignmentRestrictions/deleteDefaultRestriction'),
        (select id from fields where name = 'subjectId'),
        false),
       ((select id from urls where url = '/api/assignmentRestrictions/deleteDefaultRestriction'),
        (select id from fields where name = 'universityId'),
        false);

insert into url_fields(url_id, field_id, required)
values ((select id from urls where url = '/api/permissions/removePermission'),
        (select id from fields where name = 'userId'),
        true),
       ((select id from urls where url = '/api/permissions/removePermission'),
        (select id from fields where name = 'universityId'),
        false),
       ((select id from urls where url = '/api/permissions/removePermission'),
        (select id from fields where name = 'subjectId'),
        false),
       ((select id from urls where url = '/api/permissions/removePermission'),
        (select id from fields where name = 'functionId'),
        false),
       ((select id from urls where url = '/api/permissions/removePermission'),
        (select id from fields where name = 'userAssignmentId'),
        false);