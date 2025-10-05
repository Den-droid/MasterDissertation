package org.apiapplication.services.interfaces;

import org.apiapplication.dto.permission.PermissionDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.entities.assignment.Function;
import org.apiapplication.entities.assignment.UserAssignment;
import org.apiapplication.entities.user.User;

public interface PermissionService {
    boolean userCanAccessAssignment(User user, UserAssignment userAssignment);

    boolean userCanAccessFunction(User user, Function function);

    boolean userCanAccessSubject(User user, Subject subject);

    boolean userCanAccessUniversity(User user, University university);

    void givePermission(PermissionDto permissionDto);

    void removePermission(int permissionId);
}
