update urls
set url = '/api/assignments/{userAssignmentId}/giveAnswer'
where id = 6;

update urls
set url = '/api/assignments/{userAssignmentId}/putMark'
where id = 16;

delete
from url_fields
where url_id = 17;

delete
from urls
where id = 17;

insert into urls(url, description, method)
values ('/assignments/{userAssignmentId}/marks', 'Отримати оцінки для завдання', 0);

insert into urls(url, description, method)
values ('/assignments/restrictionTypes', 'Отримати типи обмежень для завдання', 0);