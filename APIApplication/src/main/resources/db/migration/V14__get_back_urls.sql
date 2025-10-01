update urls
set url = '/api/assignments/{userAssignmentId}'
where id = 2;

update urls
set url = '/api/assignments/{userAssignmentId}/startContinue'
where id = 4;

update urls
set url = '/api/assignments/{userAssignmentId}/finish'
where id = 5;

update urls
set url = '/api/assignments/{userAssignmentId}/answer'
where id = 6;

update urls
set url = '/api/assignments/{userAssignmentId}/answers'
where id = 7;

update urls
set url = '/api/assignments/{assignmentId}/mark'
where id = 16;

update urls
set url = '/api/users/{userId}/apiKey'
where id = 22;