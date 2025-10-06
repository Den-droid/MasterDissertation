package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.permission.PermissionDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.entities.assignment.Function;
import org.apiapplication.entities.assignment.UserAssignment;
import org.apiapplication.entities.user.User;
import org.apiapplication.entities.user.UserPermission;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.*;
import org.apiapplication.services.interfaces.PermissionService;
import org.apiapplication.services.interfaces.SessionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {
    private final UserPermissionRepository userPermissionRepository;
    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final SubjectRepository subjectRepository;
    private final UserAssignmentRepository userAssignmentRepository;
    private final FunctionRepository functionRepository;

    private final SessionService sessionService;

    public PermissionServiceImpl(UserPermissionRepository userPermissionRepository,
                                 UserRepository userRepository,
                                 UniversityRepository universityRepository,
                                 SubjectRepository subjectRepository,
                                 UserAssignmentRepository userAssignmentRepository,
                                 FunctionRepository functionRepository,
                                 SessionService sessionService) {
        this.userPermissionRepository = userPermissionRepository;
        this.userRepository = userRepository;
        this.universityRepository = universityRepository;
        this.subjectRepository = subjectRepository;
        this.userAssignmentRepository = userAssignmentRepository;
        this.functionRepository = functionRepository;

        this.sessionService = sessionService;
    }

    @Override
    public boolean userCanAccessAssignment(User user, UserAssignment userAssignment) {
        if (userAssignment.getUser().getId().equals(user.getId())) {
            return true;
        }

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

        Optional<UserPermission> userPermission = userPermissions.stream()
                .filter(up -> up.getSubject().getId().equals(subject.getId()))
                .findFirst();

        if (userPermission.isPresent()) {
            return true;
        }

        University university = subject.getUniversity();

        return userCanAccessUniversity(user, university);
    }

    @Override
    public boolean userCanAccessUniversity(User user, University university) {
        List<UserPermission> userPermissions = user.getUserPermissions();

        Optional<UserPermission> userPermission = userPermissions.stream()
                .filter(up -> up.getUniversity().getId().equals(university.getId()))
                .findFirst();

        return userPermission.isPresent();
    }

    @Override
    public void givePermission(PermissionDto permissionDto) {
        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        User user = userRepository.findById(permissionDto.userId()).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER,
                        String.valueOf(permissionDto.userId()))
        );

        if (sessionService.isUserStudent(user)) {
            throw new PermissionException();
        }

        University university = null;
        Subject subject = null;
        Function function = null;
        UserAssignment userAssignment = null;

        if (permissionDto.universityId() != null) {
            university = universityRepository.findById(permissionDto.universityId()).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                            String.valueOf(permissionDto.universityId()))
            );
        } else if (permissionDto.subjectId() != null) {
            subject = subjectRepository.findById(permissionDto.subjectId()).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.SUBJECT,
                            String.valueOf(permissionDto.subjectId()))
            );
        } else if (permissionDto.functionId() != null) {
            function = functionRepository.findById(permissionDto.functionId()).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.FUNCTION,
                            String.valueOf(permissionDto.functionId()))
            );
        } else if (permissionDto.userAssignmentId() != null) {
            userAssignment = userAssignmentRepository.findById(permissionDto.userAssignmentId()).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                            String.valueOf(permissionDto.userAssignmentId()))
            );
        }

        if (university == null && subject == null && function == null && userAssignment == null) {
            return;
        }

        UserPermission userPermission = new UserPermission();
        userPermission.setUser(user);
        userPermission.setUniversity(university);
        userPermission.setSubject(subject);
        userPermission.setFunction(function);
        userPermission.setUserAssignment(userAssignment);

        if (!user.getUserPermissions().contains(userPermission)) {
            userPermissionRepository.save(userPermission);
        }
    }

    @Override
    public void removePermission(int permissionId) {
        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        userPermissionRepository.deleteById(permissionId);
    }
}
