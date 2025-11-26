insert into url_roles(role_id, url_id)
values ((select id from roles where name = 'ADMIN'), (select id from urls where url = '/api/assignments')),
       ((select id from roles where name = 'ADMIN'),
        (select id from urls where url = '/api/assignments/{userAssignmentId}')),
       ((select id from roles where name = 'ADMIN'),
        (select id from urls where url = '/api/assignments/{userAssignmentId}/answers')),
       ((select id from roles where name = 'ADMIN'), (select id from urls where url = '/api/fields')),
       ((select id from roles where name = 'ADMIN'),
        (select id from urls where url = '/api/assignments/{userAssignmentId}/marks')),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/subjects'
                                                        and method = 0)),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/subjects'
                                                        and method = 1)),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/universities'
                                                        and method = 0)),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/universities'
                                                        and method = 1)),
       ((select id from roles where name = 'ADMIN'), (select id from urls where url = '/api/urls')),
       ((select id from roles where name = 'ADMIN'), (select id from urls where url = '/api/urls/methods')),
       ((select id from roles where name = 'ADMIN'),
        (select id from urls where url = '/api/assignmentRestrictions/restrictionTypes')),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/functions'
                                                        and method = 0)),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/functions'
                                                        and method = 1)),
       ((select id from roles where name = 'ADMIN'),
        (select id from urls where url = '/api/assignmentRestrictions/setDefaultRestriction')),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/permissions'
                                                        and method = 0)),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/permissions'
                                                        and method = 1)),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/permissions'
                                                        and method = 2)),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/permissions'
                                                        and method = 3)),
       ((select id from roles where name = 'ADMIN'),
        (select id from urls where url = '/api/assignmentRestrictions/setRestriction')),
       ((select id from roles where name = 'ADMIN'), (select id from urls where url = '/api/assignmentRestrictions')),
       ((select id from roles where name = 'ADMIN'),
        (select id
         from urls
         where url = '/api/universities/{universityId}'
           and method = 0)),
       ((select id from roles where name = 'ADMIN'),
        (select id
         from urls
         where url = '/api/universities/{universityId}'
           and method = 2)),
       ((select id from roles where name = 'ADMIN'),
        (select id
         from urls
         where url = '/api/universities/{universityId}'
           and method = 3)),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/subjects/{subjectId}'
                                                        and method = 0)),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/subjects/{subjectId}'
                                                        and method = 2)),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/subjects/{subjectId}'
                                                        and method = 3)),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/functions/{functionId}'
                                                        and method = 0)),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/functions/{functionId}'
                                                        and method = 2)),
       ((select id from roles where name = 'ADMIN'), (select id
                                                      from urls
                                                      where url = '/api/functions/{functionId}'
                                                        and method = 3)),
       ((select id from roles where name = 'ADMIN'),
        (select id from urls where url = '/api/assignmentRestrictions/defaultRestrictions')),
       ((select id from roles where name = 'ADMIN'), (select id from urls where url = '/api/users')),
       ((select id from roles where name = 'ADMIN'), (select id from urls where url = '/api/users/{userId}/approve')),
       ((select id from roles where name = 'ADMIN'), (select id from urls where url = '/api/users/{userId}/reject')),
       ((select id from roles where name = 'ADMIN'), (select id from urls where url = '/api/users/{userId}')),
       ((select id from roles where name = 'ADMIN'),
        (select id from urls where url = '/api/functions/getByAssignmentIds')),
       ((select id from roles where name = 'ADMIN'), (select id from urls where url = '/api/users/createAdmin')),

       ((select id from roles where name = 'TEACHER'), (select id from urls where url = '/api/assignments')),
       ((select id from roles where name = 'TEACHER'),
        (select id from urls where url = '/api/assignments/{userAssignmentId}')),
       ((select id from roles where name = 'TEACHER'),
        (select id from urls where url = '/api/assignments/{userAssignmentId}/answers')),
       ((select id from roles where name = 'TEACHER'), (select id from urls where url = '/api/fields')),
       ((select id from roles where name = 'TEACHER'),
        (select id from urls where url = '/api/assignments/{userAssignmentId}/putMark')),
       ((select id from roles where name = 'TEACHER'),
        (select id from urls where url = '/api/assignments/{userAssignmentId}/marks')),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/subjects'
                                                          and method = 0)),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/subjects'
                                                          and method = 1)),
       ((select id from roles where name = 'TEACHER'), (select id from urls where url = '/api/urls')),
       ((select id from roles where name = 'TEACHER'), (select id from urls where url = '/api/urls/methods')),
       ((select id from roles where name = 'TEACHER'),
        (select id from urls where url = '/api/assignmentRestrictions/restrictionTypes')),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/functions'
                                                          and method = 0)),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/functions'
                                                          and method = 1)),
       ((select id from roles where name = 'TEACHER'),
        (select id from urls where url = '/api/assignmentRestrictions/setDefaultRestriction')),
       ((select id from roles where name = 'TEACHER'),
        (select id from urls where url = '/api/assignmentRestrictions/setRestriction')),
       ((select id from roles where name = 'TEACHER'), (select id from urls where url = '/api/assignmentRestrictions')),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/subjects/{subjectId}'
                                                          and method = 0)),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/subjects/{subjectId}'
                                                          and method = 2)),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/subjects/{subjectId}'
                                                          and method = 3)),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/functions/{functionId}'
                                                          and method = 0)),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/functions/{functionId}'
                                                          and method = 2)),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/functions/{functionId}'
                                                          and method = 3)),
       ((select id from roles where name = 'TEACHER'),
        (select id from urls where url = '/api/assignmentRestrictions/defaultRestrictions')),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/groups'
                                                          and method = 0)),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/groups'
                                                          and method = 1)),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/groups/{groupId}'
                                                          and method = 0)),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/groups/{groupId}'
                                                          and method = 2)),
       ((select id from roles where name = 'TEACHER'), (select id
                                                        from urls
                                                        where url = '/api/groups/{groupId}'
                                                          and method = 3)),
       ((select id from roles where name = 'TEACHER'),
        (select id from urls where url = '/api/groups/{groupId}/addStudents')),
       ((select id from roles where name = 'TEACHER'),
        (select id from urls where url = '/api/groups/{groupId}/removeStudents')),
       ((select id from roles where name = 'TEACHER'),
        (select id from urls where url = '/api/groups/{groupId}/addSubjects')),
       ((select id from roles where name = 'TEACHER'),
        (select id from urls where url = '/api/groups/{groupId}/removeSubjects')),
       ((select id from roles where name = 'TEACHER'),
        (select id from urls where url = '/api/assignments/assignToGroup')),

       ((select id from roles where name = 'STUDENT'), (select id from urls where url = '/api/assignments')),
       ((select id from roles where name = 'STUDENT'),
        (select id from urls where url = '/api/assignments/{userAssignmentId}')),
       ((select id from roles where name = 'STUDENT'), (select id from urls where url = '/api/assignments/assign')),
       ((select id from roles where name = 'STUDENT'),
        (select id from urls where url = '/api/assignments/{userAssignmentId}/startContinue')),
       ((select id from roles where name = 'STUDENT'),
        (select id from urls where url = '/api/assignments/{userAssignmentId}/finish')),
       ((select id from roles where name = 'STUDENT'),
        (select id from urls where url = '/api/assignments/{userAssignmentId}/giveAnswer')),
       ((select id from roles where name = 'STUDENT'),
        (select id from urls where url = '/api/assignments/{userAssignmentId}/answers')),
       ((select id from roles where name = 'STUDENT'), (select id from urls where url = '/api/fields')),
       ((select id from roles where name = 'STUDENT'),
        (select id from urls where url = '/api/assignments/{userAssignmentId}/marks')),
       ((select id from roles where name = 'STUDENT'), (select id from urls where url = '/api/urls')),
       ((select id from roles where name = 'STUDENT'), (select id from urls where url = '/api/urls/methods')),
       ((select id from roles where name = 'STUDENT'), (select id
                                                        from urls
                                                        where url = '/api/subjects'
                                                          and method = 0)),
       ((select id from roles where name = 'STUDENT'),
        (select id from urls where url = '/api/assignmentRestrictions/restrictionTypes'));