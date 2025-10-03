package org.apiapplication.services.implementations;

import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.entities.assignment.Function;
import org.apiapplication.entities.assignment.UserAssignment;
import org.apiapplication.entities.user.User;
import org.apiapplication.entities.user.UserPermission;
import org.apiapplication.repositories.UserPermissionRepository;
import org.apiapplication.services.interfaces.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionServiceImpl implements PermissionService {
    UserPermissionRepository userPermissionRepository;

    public PermissionServiceImpl(UserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }

    @Override
    public boolean studentCanAccessAssignment(User user, UserAssignment userAssignment) {
        return userAssignment.getUser().getId().equals(user.getId());
    }

    @Override
    public boolean userCanAccessAssignment(User user, UserAssignment userAssignment) {
        List<UserPermission> userPermissions = user.getUserPermissions();

        Optional<UserPermission> userPermission = userPermissions.stream()
                .filter(up -> up.getUserAssignment().getId().equals(userAssignment.getId()))
                .findFirst();

        if (userPermission.isPresent()) {
            return true;
        }

        Function function = userAssignment.getFunction();

        return userCanAccessFunction(user, function);
    }

    @Override
    public boolean userCanAccessFunction(User user, Function function) {
        List<UserPermission> userPermissions = user.getUserPermissions();

        Optional<UserPermission> userPermission = userPermissions.stream()
                .filter(up -> up.getFunction().getId().equals(function.getId()))
                .findFirst();

        if (userPermission.isPresent()) {
            return true;
        }

        Subject subject = function.getSubject();

        return userCanAccessSubject(user, subject);
    }

    @Override
    public boolean userCanAccessSubject(User user, Subject subject) {
        List<UserPermission> userPermissions = user.getUserPermissions();

        if (subject != null) {
            Optional<UserPermission> userPermission = userPermissions.stream()
                    .filter(up -> up.getSubject().getId().equals(subject.getId()))
                    .findFirst();

            if (userPermission.isPresent()) {
                return true;
            }

            University university = subject.getUniversity();

            return userCanAccessUniversity(user, university);
        }

        return false;
    }

    @Override
    public boolean userCanAccessUniversity(User user, University university) {
        List<UserPermission> userPermissions = user.getUserPermissions();

        if (university != null) {
            Optional<UserPermission> userPermission = userPermissions.stream()
                    .filter(up -> up.getUniversity().getId().equals(university.getId()))
                    .findFirst();

            return userPermission.isPresent();
        }

        return false;
    }
}
