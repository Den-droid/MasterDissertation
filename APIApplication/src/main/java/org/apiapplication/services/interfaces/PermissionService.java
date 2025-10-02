package org.apiapplication.services.interfaces;

import org.apiapplication.entities.assignment.UserAssignment;
import org.apiapplication.entities.user.User;

public interface PermissionService {
    boolean studentCanAccessAssignment(User user, UserAssignment userAssignment);
    boolean userCanAccessAssignment(User user, UserAssignment userAssignment);
}
