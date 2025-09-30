update urls
set url = '/api/assignments/{userAssignmentId:\\d+}'
where id = 2;

update urls
set url = '/api/assignments/{userAssignmentId:\\d+}/startContinue'
where id = 4;

update urls
set url = '/api/assignments/{userAssignmentId:\\d+}/finish'
where id = 5;

update urls
set url = '/api/assignments/{userAssignmentId:\\d+}/answer'
where id = 6;

update urls
set url = '/api/assignments/{userAssignmentId:\\d+}/answers'
where id = 7;

update urls
set url = '/api/assignments/{assignmentId:\\d+}/mark'
where id = 16;

update urls
set url = '/api/users/{userId:\\d+}/apiKey'
where id = 22;