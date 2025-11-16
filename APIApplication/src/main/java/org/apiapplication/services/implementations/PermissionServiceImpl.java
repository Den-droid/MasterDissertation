package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.permission.PermissionDto;
import org.apiapplication.dto.permission.UpdatePermissionDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.entities.assignment.Function;
import org.apiapplication.entities.assignment.UserAssignment;
import org.apiapplication.entities.user.User;
import org.apiapplication.entities.user.UserPermission;
import org.apiapplication.enums.UserRole;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.*;
import org.apiapplication.services.interfaces.PermissionService;
import org.apiapplication.services.interfaces.SessionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<PermissionDto> get(Integer userId) {
        if (userId != null) {
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.USER, String.valueOf(userId))
            );

            if (sessionService.isCurrentUser(userId) ||
                    sessionService.isUserAdmin(sessionService.getCurrentUser())) {
                List<UserPermission> userPermissions = user.getUserPermissions();
                return userPermissions.stream()
                        .map(this::getPermissionDto)
                        .toList();
            } else {
                return List.of();
            }
        } else {
            if (sessionService.isUserAdmin(sessionService.getCurrentUser())) {
                List<UserPermission> userPermissions = userPermissionRepository.findAll();
                return userPermissions.stream().map(this::getPermissionDto).toList();
            } else {
                User user = sessionService.getCurrentUser();
                List<UserPermission> userPermissions = user.getUserPermissions();
                return userPermissions.stream().map(this::getPermissionDto).toList();
            }
        }
    }

    @Override
    public boolean userCanAccessAssignment(User user, UserAssignment userAssignment) {
        if (userAssignment.getUser().getId().equals(user.getId())) {
            return true;
        }

        List<UserPermission> userPermissions = user.getUserPermissions();

        Optional<UserPermission> userPermission = userPermissions.stream()
                .filter(up -> up.getUserAssignment() != null)
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
                .filter(up -> up.getFunction() != null)
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
                .filter(up -> up.getSubject() != null)
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
                .filter(up -> up.getUniversity() != null)
                .filter(up -> up.getUniversity().getId().equals(university.getId()))
                .findFirst();

        return userPermission.isPresent();
    }

    @Override
    public void updatePermissions(UpdatePermissionDto dto) {
        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        User user = userRepository.findById(dto.userId()).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER,
                        String.valueOf(dto.userId()))
        );

        if (sessionService.isUserStudent(user)) {
            throw new PermissionException();
        }

        List<UserPermission> userPermissionsToAdd = getUserPermissionsToAdd(dto, user);
        List<UserPermission> userPermissionsToRemove = new ArrayList<>();

        user.getUserPermissions().stream()
                .filter(up -> up.getUniversity() != null)
                .filter(up -> !dto.universityIds().contains(up.getUniversity().getId())
                        && up.getUser().getId().equals(user.getId()))
                .forEach(userPermissionsToRemove::add);

        user.getUserPermissions().stream()
                .filter(up -> up.getSubject() != null)
                .filter(up -> !dto.subjectIds().contains(up.getSubject().getId())
                        && up.getUser().getId().equals(user.getId()))
                .forEach(userPermissionsToRemove::add);

        user.getUserPermissions().stream()
                .filter(up -> up.getFunction() != null)
                .filter(up -> !dto.functionIds().contains(up.getFunction().getId())
                        && up.getUser().getId().equals(user.getId()))
                .forEach(userPermissionsToRemove::add);

        user.getUserPermissions().stream()
                .filter(up -> up.getUserAssignment() != null)
                .filter(up -> !dto.userAssignmentIds().contains(
                        up.getUserAssignment().getId())
                        && up.getUser().getId().equals(user.getId()))
                .forEach(userPermissionsToRemove::add);

        userPermissionRepository.deleteAll(userPermissionsToRemove);
        userPermissionRepository.saveAll(userPermissionsToAdd);
    }

    @Override
    public void removePermissions(UpdatePermissionDto dto) {
        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        User user = userRepository.findById(dto.userId()).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER,
                        String.valueOf(dto.userId()))
        );

        List<UserPermission> userPermissionsToRemove = new ArrayList<>();
        user.getUserPermissions().stream()
                .filter(up -> up.getUniversity() != null)
                .filter(up -> dto.universityIds().contains(up.getUniversity().getId())
                        && up.getUser().getId().equals(user.getId()))
                .forEach(userPermissionsToRemove::add);

        user.getUserPermissions().stream()
                .filter(up -> up.getSubject() != null)
                .filter(up -> dto.subjectIds().contains(up.getSubject().getId())
                        && up.getUser().getId().equals(user.getId()))
                .forEach(userPermissionsToRemove::add);

        user.getUserPermissions().stream()
                .filter(up -> up.getFunction() != null)
                .filter(up -> dto.functionIds().contains(up.getFunction().getId())
                        && up.getUser().getId().equals(user.getId()))
                .forEach(userPermissionsToRemove::add);

        user.getUserPermissions().stream()
                .filter(up -> up.getUserAssignment() != null)
                .filter(up -> dto.userAssignmentIds().contains(
                        up.getUserAssignment().getId())
                        && up.getUser().getId().equals(user.getId()))
                .forEach(userPermissionsToRemove::add);

        userPermissionRepository.deleteAll(userPermissionsToRemove);
    }

    @Override
    public void givePermissions(UpdatePermissionDto dto) {
        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        User user = userRepository.findById(dto.userId()).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER,
                        String.valueOf(dto.userId()))
        );

        List<UserPermission> userPermissionsToAdd = getUserPermissionsToAdd(dto, user);

        userPermissionRepository.saveAll(userPermissionsToAdd);
    }

    private List<UserPermission> getUserPermissionsToAdd(UpdatePermissionDto dto,
                                                         User user) {
        List<UserPermission> userPermissionsToAdd = new ArrayList<>();
        for (int universityId : dto.universityIds()) {
            if (user.getRoles().get(0).getName().equals(UserRole.TEACHER)) {
                throw new PermissionException();
            }

            University university = universityRepository.findById(universityId).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                            String.valueOf(universityId))
            );

            UserPermission userPermission = new UserPermission();
            userPermission.setUser(user);
            userPermission.setUniversity(university);

            if (!user.getUserPermissions().contains(userPermission)) {
                userPermissionsToAdd.add(userPermission);
            }
        }

        for (int subjectId : dto.subjectIds()) {
            Subject subject = subjectRepository.findById(subjectId).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.SUBJECT,
                            String.valueOf(subjectId))
            );

            UserPermission userPermission = new UserPermission();
            userPermission.setUser(user);
            userPermission.setSubject(subject);

            if (!user.getUserPermissions().contains(userPermission)) {
                userPermissionsToAdd.add(userPermission);
            }
        }

        for (int functionId : dto.functionIds()) {
            Function function = functionRepository.findById(functionId).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.FUNCTION,
                            String.valueOf(functionId))
            );

            UserPermission userPermission = new UserPermission();
            userPermission.setUser(user);
            userPermission.setFunction(function);

            if (!user.getUserPermissions().contains(userPermission)) {
                userPermissionsToAdd.add(userPermission);
            }
        }

        for (int assignmentId : dto.userAssignmentIds()) {
            UserAssignment userAssignment = userAssignmentRepository.findById(assignmentId)
                    .orElseThrow(
                            () -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                                    String.valueOf(assignmentId))
                    );

            UserPermission userPermission = new UserPermission();
            userPermission.setUser(user);
            userPermission.setUserAssignment(userAssignment);

            if (!user.getUserPermissions().contains(userPermission)) {
                userPermissionsToAdd.add(userPermission);
            }
        }

        return userPermissionsToAdd;
    }

    private PermissionDto getPermissionDto(UserPermission userPermission) {
        return new PermissionDto(userPermission.getId(), userPermission.getUser().getId(),
                userPermission.getUniversity() != null ? userPermission.getUniversity().getId() : null,
                userPermission.getSubject() != null ? userPermission.getSubject().getId() : null,
                userPermission.getFunction() != null ? userPermission.getFunction().getId() : null,
                userPermission.getUserAssignment() != null ? userPermission.getUserAssignment().getId()
                        : null);
    }
}
