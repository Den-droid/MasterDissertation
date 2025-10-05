insert into fields(name, label, description, type)
values ('permissionId', 'Ідентифікатор права', 'Унікальний числовий ідентифікатор права', 0),
       ('defaultRestrictionId', 'Ідентифікатор обмеження по замовчуванню',
        'Унікальний числовий ідентифікатор обмеження по замовчуванню', 0);

insert into url_fields(url_id, field_id, required)
values ((select id from urls where url = '/api/permissions/removePermission'),
        (select id from fields where name = 'permissionId'),
        true),
       ((select id from urls where url = '/api/assignmentRestrictions/deleteDefaultRestriction'),
        (select id from fields where name = 'defaultRestrictionId'),
        true);