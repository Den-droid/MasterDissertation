ALTER TABLE default_assignment_restrictions
    ADD deadline TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE url_fields
    ALTER COLUMN required SET NOT NULL;

ALTER TABLE default_assignment_restrictions
    DROP COLUMN minutes_to_deadline;

update urls
set url = '/api/assignmentRestrictions/restrictionTypes'
where url = '/api/assignments/restrictionTypes';

insert into urls(url, description, method)
values ('/api/assignmentRestrictions/setDefaultRestriction', 'Задати обмеження по замовчуванню для виконання завдання',
        1),
       ('/api/permissions/givePermission', 'Надати доступ користувачу до ресурсів', 1),
       ('/api/assignmentRestrictions/setRestriction', 'Задати обмеження для виконання завдання', 2);

insert into fields(name, label, description, type)
values ('minutesForAttempt', 'Хвилин для однієї спроби', 'Хвилин для виконання однієї спроби в завданні', 0),
       ('functionId', 'Ідентифікатор функції', 'Унікальний числовий ідентифікатор функції', 0);

insert into url_fields(url_id, field_id, required)
values ((select id from urls where url = '/api/assignmentRestrictions/setDefaultRestriction'),
        (select id from fields where name = 'restrictionType'),
        true),
       ((select id from urls where url = '/api/assignmentRestrictions/setDefaultRestriction'),
        (select id from fields where name = 'functionId'),
        false),
       ((select id from urls where url = '/api/assignmentRestrictions/setDefaultRestriction'),
        (select id from fields where name = 'subjectId'),
        false),
       ((select id from urls where url = '/api/assignmentRestrictions/setDefaultRestriction'),
        (select id from fields where name = 'universityId'),
        false),
       ((select id from urls where url = '/api/assignmentRestrictions/setDefaultRestriction'),
        (select id from fields where name = 'attemptsRemaining'),
        false),
       ((select id from urls where url = '/api/assignmentRestrictions/setDefaultRestriction'),
        (select id from fields where name = 'minutesForAttempt'),
        false),
       ((select id from urls where url = '/api/assignmentRestrictions/setDefaultRestriction'),
        (select id from fields where name = 'deadline'),
        false);

insert into url_fields(url_id, field_id, required)
values ((select id from urls where url = '/api/assignmentRestrictions/setRestriction'),
        (select id from fields where name = 'restrictionType'),
        true),
       ((select id from urls where url = '/api/assignmentRestrictions/setRestriction'),
        (select id from fields where name = 'functionId'),
        false),
       ((select id from urls where url = '/api/assignmentRestrictions/setRestriction'),
        (select id from fields where name = 'subjectId'),
        false),
       ((select id from urls where url = '/api/assignmentRestrictions/setRestriction'),
        (select id from fields where name = 'universityId'),
        false),
       ((select id from urls where url = '/api/assignmentRestrictions/setRestriction'),
        (select id from fields where name = 'userAssignmentId'),
        false),
       ((select id from urls where url = '/api/assignmentRestrictions/setRestriction'),
        (select id from fields where name = 'attemptsRemaining'),
        false),
       ((select id from urls where url = '/api/assignmentRestrictions/setRestriction'),
        (select id from fields where name = 'minutesForAttempt'),
        false),
       ((select id from urls where url = '/api/assignmentRestrictions/setRestriction'),
        (select id from fields where name = 'deadline'),
        false);

insert into url_fields (url_id, field_id, required)
values ((select id from urls where url = '/api/permissions/givePermission'),
        (select id from fields where name = 'userId'),
        true),
       ((select id from urls where url = '/api/permissions/givePermission'),
        (select id from fields where name = 'universityId'),
        false),
       ((select id from urls where url = '/api/permissions/givePermission'),
        (select id from fields where name = 'subjectId'),
        false),
       ((select id from urls where url = '/api/permissions/givePermission'),
        (select id from fields where name = 'functionId'),
        false),
       ((select id from urls where url = '/api/permissions/givePermission'),
        (select id from fields where name = 'userAssignmentId'),
        false);