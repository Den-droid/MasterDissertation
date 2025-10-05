package org.apiapplication.services.interfaces;

import org.apiapplication.entities.user.User;

public interface SessionService {
    User getCurrentUser();

    boolean isUserAdmin(User user);

    boolean isUserTeacher(User user);

    boolean isUserStudent(User user);

    boolean isCurrentUser(int userId);

    boolean isCurrentUser(User user);
}
