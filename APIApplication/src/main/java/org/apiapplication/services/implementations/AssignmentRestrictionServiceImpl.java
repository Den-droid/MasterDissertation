package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.restriction.DefaultRestrictionDto;
import org.apiapplication.dto.restriction.RestrictionDto;
import org.apiapplication.dto.restriction.RestrictionTypeDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.entities.assignment.DefaultAssignmentRestriction;
import org.apiapplication.entities.assignment.UserAssignment;
import org.apiapplication.entities.function.Function;
import org.apiapplication.entities.maze.Maze;
import org.apiapplication.entities.user.UserPermission;
import org.apiapplication.enums.AssignmentRestrictionType;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.*;
import org.apiapplication.services.interfaces.AssignmentRestrictionService;
import org.apiapplication.services.interfaces.PermissionService;
import org.apiapplication.services.interfaces.SessionService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class AssignmentRestrictionServiceImpl implements AssignmentRestrictionService {
    private final DefaultAssignmentRestrictionRepository defaultAssignmentRestrictionRepository;
    private final FunctionRepository functionRepository;
    private final SubjectRepository subjectRepository;
    private final UniversityRepository universityRepository;
    private final UserAssignmentRepository userAssignmentRepository;
    private final MazeRepository mazeRepository;

    private final SessionService sessionService;
    private final PermissionService permissionService;

    public AssignmentRestrictionServiceImpl(DefaultAssignmentRestrictionRepository defaultAssignmentRestrictionRepository,
                                            FunctionRepository functionRepository,
                                            SubjectRepository subjectRepository,
                                            UniversityRepository universityRepository,
                                            MazeRepository mazeRepository,
                                            SessionService sessionService,
                                            PermissionService permissionService,
                                            UserAssignmentRepository userAssignmentRepository) {
        this.defaultAssignmentRestrictionRepository = defaultAssignmentRestrictionRepository;
        this.functionRepository = functionRepository;
        this.subjectRepository = subjectRepository;
        this.universityRepository = universityRepository;
        this.userAssignmentRepository = userAssignmentRepository;
        this.mazeRepository = mazeRepository;

        this.sessionService = sessionService;
        this.permissionService = permissionService;
    }

    @Override
    public List<DefaultRestrictionDto> getDefault(Integer functionId, Integer subjectId,
                                                  Integer universityId, Integer mazeId) {
        if (functionId != null) {
            Function function = functionRepository.findById(functionId)
                    .orElseThrow(() -> new EntityWithIdNotFoundException(
                            EntityName.FUNCTION, String.valueOf(functionId)
                    ));

            if (!permissionService.userCanAccessFunction(sessionService.getCurrentUser(),
                    function)) {
                throw new PermissionException();
            }

            DefaultAssignmentRestriction restriction = getDefaultRestrictionForFunction(function);
            if (restriction != null) {
                return List.of(getDefaultAssignmentRestrictionDto(restriction));
            } else {
                return List.of();
            }
        } else if (mazeId != null) {
            Maze maze = mazeRepository.findById(mazeId)
                    .orElseThrow(() -> new EntityWithIdNotFoundException(
                            EntityName.MAZE, String.valueOf(mazeId)
                    ));

            if (!permissionService.userCanAccessMaze(sessionService.getCurrentUser(),
                    maze)) {
                throw new PermissionException();
            }

            DefaultAssignmentRestriction restriction = getDefaultRestrictionForMaze(maze);
            if (restriction != null) {
                return List.of(getDefaultAssignmentRestrictionDto(restriction));
            } else {
                return List.of();
            }
        } else if (subjectId != null) {
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new EntityWithIdNotFoundException(
                            EntityName.SUBJECT, String.valueOf(subjectId)
                    ));

            if (!permissionService.userCanAccessSubject(sessionService.getCurrentUser(),
                    subject)) {
                throw new PermissionException();
            }

            DefaultAssignmentRestriction restriction = getDefaultRestrictionForSubject(subject);
            if (restriction != null) {
                return List.of(getDefaultAssignmentRestrictionDto(restriction));
            } else {
                return List.of();
            }
        } else if (universityId != null) {
            University university = universityRepository.findById(universityId)
                    .orElseThrow(() -> new EntityWithIdNotFoundException(
                            EntityName.UNIVERSITY, String.valueOf(universityId)
                    ));

            if (!permissionService.userCanAccessUniversity(sessionService.getCurrentUser(),
                    university)) {
                throw new PermissionException();
            }

            DefaultAssignmentRestriction restriction = getDefaultRestrictionForUniversity(university);
            if (restriction != null) {
                return List.of(getDefaultAssignmentRestrictionDto(restriction));
            } else {
                return List.of();
            }
        } else {
            List<UserPermission> permissions = sessionService.getCurrentUser().getUserPermissions();
            return permissions.stream()
                    .map(p -> {
                        if (p.getUniversity() != null) {
                            return p.getUniversity().getDefaultAssignmentRestrictions();
                        } else if (p.getSubject() != null) {
                            return p.getSubject().getDefaultAssignmentRestrictions();
                        } else {
                            return p.getFunction().getDefaultAssignmentRestrictions();
                        }
                    })
                    .flatMap(Collection::stream)
                    .map(this::getDefaultAssignmentRestrictionDto)
                    .toList();
        }
    }

    @Override
    public RestrictionDto getCurrent(Integer userAssignmentId) {
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                        String.valueOf(userAssignmentId)));

        return getAssignmentRestrictionDto(userAssignment);
    }

    @Override
    public DefaultAssignmentRestriction getDefaultRestrictionForMaze(Maze maze) {
        List<DefaultAssignmentRestriction> defaultAssignmentRestrictions =
                defaultAssignmentRestrictionRepository.findAll();

        Optional<DefaultAssignmentRestriction> defaultRestriction = defaultAssignmentRestrictions.stream()
                .filter(restriction -> restriction.getMaze() != null &&
                        Objects.equals(restriction.getMaze().getId(),
                                maze.getId()))
                .findFirst();

        if (defaultRestriction.isPresent()) {
            return defaultRestriction.get();
        }

        University university = maze.getUniversity();

        return getDefaultRestrictionForUniversity(university);
    }

    @Override
    public DefaultAssignmentRestriction getDefaultRestrictionForFunction(Function function) {
        List<DefaultAssignmentRestriction> defaultAssignmentRestrictions =
                defaultAssignmentRestrictionRepository.findAll();

        Optional<DefaultAssignmentRestriction> defaultRestriction = defaultAssignmentRestrictions.stream()
                .filter(restriction -> restriction.getFunction() != null &&
                        Objects.equals(restriction.getFunction().getId(),
                                function.getId()))
                .findFirst();

        if (defaultRestriction.isPresent()) {
            return defaultRestriction.get();
        }

        Subject subject = function.getSubject();

        return getDefaultRestrictionForSubject(subject);
    }

    @Override
    public DefaultAssignmentRestriction getDefaultRestrictionForSubject(Subject subject) {
        List<DefaultAssignmentRestriction> defaultAssignmentRestrictions =
                defaultAssignmentRestrictionRepository.findAll();

        Optional<DefaultAssignmentRestriction> defaultRestriction = defaultAssignmentRestrictions.stream()
                .filter(restriction -> restriction.getSubject() != null &&
                        Objects.equals(restriction.getSubject().getId(),
                                subject.getId()))
                .findFirst();

        if (defaultRestriction.isPresent()) {
            return defaultRestriction.get();
        }

        University university = subject.getUniversity();

        return getDefaultRestrictionForUniversity(university);
    }

    @Override
    public DefaultAssignmentRestriction getDefaultRestrictionForUniversity(University university) {
        List<DefaultAssignmentRestriction> defaultAssignmentRestrictions =
                defaultAssignmentRestrictionRepository.findAll();

        Optional<DefaultAssignmentRestriction> defaultRestriction = defaultAssignmentRestrictions.stream()
                .filter(restriction -> restriction.getUniversity() != null &&
                        Objects.equals(restriction.getUniversity().getId(),
                                university.getId()))
                .findFirst();

        return defaultRestriction.orElse(null);
    }

    @Override
    public DefaultAssignmentRestriction getDefaultRestriction() {
        DefaultAssignmentRestriction defaultAssignmentRestriction = new DefaultAssignmentRestriction();
        defaultAssignmentRestriction.setAttemptsRemaining(10);
        defaultAssignmentRestriction.setAssignmentRestrictionType(AssignmentRestrictionType.N_ATTEMPTS);

        return defaultAssignmentRestriction;
    }

    @Override
    public void setDefaultRestriction(DefaultRestrictionDto dto) {
        Function function = null;
        Subject subject = null;
        University university = null;
        Maze maze = null;

        if (dto.functionId() != null) {
            function = functionRepository.findById(dto.functionId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FUNCTION,
                            String.valueOf(dto.functionId())));

            if (!permissionService.userCanAccessFunction(sessionService.getCurrentUser(),
                    function)) {
                throw new PermissionException();
            }
        } else if (dto.mazeId() != null) {
            maze = mazeRepository.findById(dto.mazeId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.MAZE,
                            String.valueOf(dto.mazeId())));

            if (!permissionService.userCanAccessMaze(sessionService.getCurrentUser(),
                    maze)) {
                throw new PermissionException();
            }
        } else if (dto.subjectId() != null) {
            subject = subjectRepository.findById(dto.subjectId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.SUBJECT,
                            String.valueOf(dto.subjectId())));

            if (!permissionService.userCanAccessSubject(sessionService.getCurrentUser(),
                    subject)) {
                throw new PermissionException();
            }
        } else if (dto.universityId() != null) {
            university = universityRepository.findById(dto.universityId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                            String.valueOf(dto.universityId())));

            if (!permissionService.userCanAccessUniversity(sessionService.getCurrentUser(),
                    university)) {
                throw new PermissionException();
            }
        }

        if (university == null && subject == null && function == null && maze == null) {
            return;
        }

        DefaultAssignmentRestriction defaultAssignmentRestriction;

        Optional<DefaultAssignmentRestriction> existingRestriction =
                defaultAssignmentRestrictionRepository.findAll().stream()
                        .filter(dar ->
                                (dar.getUniversity() != null &&
                                        dar.getUniversity().getId().equals(dto.universityId())) ||
                                        (dar.getSubject() != null &&
                                                dar.getSubject().getId().equals(dto.subjectId())) ||
                                        (dar.getFunction() != null &&
                                                dar.getFunction().getId().equals(dto.functionId())) ||
                                        (dar.getMaze() != null &&
                                                dar.getMaze().getId().equals(dto.mazeId())))
                        .findFirst();

        if (existingRestriction.isPresent()) {
            defaultAssignmentRestriction = existingRestriction.get();
        } else {
            defaultAssignmentRestriction = new DefaultAssignmentRestriction();
            defaultAssignmentRestriction.setFunction(function);
            defaultAssignmentRestriction.setSubject(subject);
            defaultAssignmentRestriction.setUniversity(university);
            defaultAssignmentRestriction.setMaze(maze);
        }

        setAssignmentRestriction(dto, defaultAssignmentRestriction);
        defaultAssignmentRestrictionRepository.save(defaultAssignmentRestriction);
    }

    @Override
    public void setRestriction(RestrictionDto dto) {
        List<UserAssignment> userAssignments = new ArrayList<>();

        UserAssignment userAssignment = null;
        Function function = null;
        Subject subject = null;
        University university = null;
        Maze maze = null;

        if (dto.userAssignmentId() != null) {
            userAssignment = userAssignmentRepository.findById(dto.userAssignmentId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                            String.valueOf(dto.userAssignmentId())));

            if (!permissionService.userCanAccessAssignment(sessionService.getCurrentUser(),
                    userAssignment)) {
                throw new PermissionException();
            }

            userAssignments.add(userAssignment);
        } else if (dto.functionId() != null) {
            function = functionRepository.findById(dto.functionId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FUNCTION,
                            String.valueOf(dto.functionId())));

            if (!permissionService.userCanAccessFunction(sessionService.getCurrentUser(),
                    function)) {
                throw new PermissionException();
            }

            userAssignments.addAll(function.getUserAssignments());
        } else if (dto.mazeId() != null) {
            maze = mazeRepository.findById(dto.mazeId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.MAZE,
                            String.valueOf(dto.mazeId())));

            if (!permissionService.userCanAccessMaze(sessionService.getCurrentUser(),
                    maze)) {
                throw new PermissionException();
            }

            userAssignments.addAll(maze.getUserAssignments());
        } else if (dto.subjectId() != null) {
            subject = subjectRepository.findById(dto.subjectId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.SUBJECT,
                            String.valueOf(dto.subjectId())));

            if (!permissionService.userCanAccessSubject(sessionService.getCurrentUser(),
                    subject)) {
                throw new PermissionException();
            }

            subject.getFunctions().stream()
                    .flatMap(f -> f.getUserAssignments().stream())
                    .forEach(userAssignments::add);
        } else if (dto.universityId() != null) {
            university = universityRepository.findById(dto.universityId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                            String.valueOf(dto.universityId())));

            if (!permissionService.userCanAccessUniversity(sessionService.getCurrentUser(),
                    university)) {
                throw new PermissionException();
            }

            university.getSubjects().stream()
                    .flatMap(s -> s.getFunctions().stream())
                    .flatMap(f -> f.getUserAssignments().stream())
                    .forEach(userAssignments::add);
        }

        if (university == null && subject == null && function == null && userAssignment == null
                && maze == null) {
            return;
        }

        for (UserAssignment ua : userAssignments) {
            setAssignmentRestriction(dto, ua);
        }

        userAssignmentRepository.saveAll(userAssignments);
    }

    @Override
    public List<RestrictionTypeDto> getRestrictionTypes() {
        if (sessionService.getCurrentUser() == null) {
            throw new PermissionException();
        }

        return Arrays.stream(AssignmentRestrictionType.values())
                .map(rt -> new RestrictionTypeDto(rt.ordinal(), rt.name()))
                .toList();
    }

    private DefaultRestrictionDto getDefaultAssignmentRestrictionDto(DefaultAssignmentRestriction
                                                                             defaultAssignmentRestriction) {
        return new DefaultRestrictionDto(defaultAssignmentRestriction.getId(),
                defaultAssignmentRestriction.getAssignmentRestrictionType().ordinal(),
                defaultAssignmentRestriction.getFunction() != null ?
                        defaultAssignmentRestriction.getFunction().getId() : null,
                defaultAssignmentRestriction.getSubject() != null ?
                        defaultAssignmentRestriction.getSubject().getId() : null,
                defaultAssignmentRestriction.getUniversity() != null ?
                        defaultAssignmentRestriction.getUniversity().getId() : null,
                defaultAssignmentRestriction.getMaze() != null ?
                        defaultAssignmentRestriction.getMaze().getId() : null,
                defaultAssignmentRestriction.getAttemptsRemaining(),
                defaultAssignmentRestriction.getMinutesForAttempt(),
                defaultAssignmentRestriction.getDeadline()
        );
    }

    private RestrictionDto getAssignmentRestrictionDto(UserAssignment userAssignment) {
        return new RestrictionDto(userAssignment.getRestrictionType().ordinal(),
                null, null, null, userAssignment.getId(), null,
                userAssignment.getAttemptsRemaining(),
                userAssignment.getMinutesForAttempt(),
                userAssignment.getDeadline()
        );
    }

    private void setAssignmentRestriction(RestrictionDto dto, UserAssignment userAssignment) {
        userAssignment.setRestrictionType(
                Arrays.stream(AssignmentRestrictionType.values())
                        .filter(x -> dto.restrictionType()
                                == x.ordinal())
                        .findFirst().orElseThrow(() ->
                                new EntityWithIdNotFoundException(EntityName.RESTRICTION_TYPE,
                                        String.valueOf(dto.restrictionType())))
        );

        if (dto.restrictionType() == AssignmentRestrictionType.N_ATTEMPTS.ordinal()) {
            userAssignment.setRestrictionType(
                    AssignmentRestrictionType.N_ATTEMPTS);
            if (dto.attemptsRemaining() != null)
                userAssignment.setAttemptsRemaining(
                        dto.attemptsRemaining());
        } else if (dto.restrictionType() == AssignmentRestrictionType.DEADLINE.ordinal()) {
            userAssignment.setRestrictionType(
                    AssignmentRestrictionType.DEADLINE);
            if (dto.deadline() != null)
                userAssignment.setDeadline(dto.deadline());
        } else {
            userAssignment.setRestrictionType(
                    AssignmentRestrictionType.ATTEMPT_PER_N_MINUTES);
            if (dto.minutesForAttempt() != null)
                userAssignment.setMinutesForAttempt(
                        dto.minutesForAttempt());
        }
    }

    private void setAssignmentRestriction(DefaultRestrictionDto dto,
                                          DefaultAssignmentRestriction defaultAssignmentRestriction) {
        defaultAssignmentRestriction.setAssignmentRestrictionType(
                Arrays.stream(AssignmentRestrictionType.values())
                        .filter(x -> dto.restrictionType()
                                == x.ordinal())
                        .findFirst().orElseThrow(() ->
                                new EntityWithIdNotFoundException(EntityName.RESTRICTION_TYPE,
                                        String.valueOf(dto.restrictionType())))
        );

        if (dto.restrictionType() == AssignmentRestrictionType.N_ATTEMPTS.ordinal()) {
            defaultAssignmentRestriction.setAssignmentRestrictionType(
                    AssignmentRestrictionType.N_ATTEMPTS);
            if (dto.attemptsRemaining() != null)
                defaultAssignmentRestriction.setAttemptsRemaining(
                        dto.attemptsRemaining());
        } else if (dto.restrictionType() == AssignmentRestrictionType.DEADLINE.ordinal()) {
            defaultAssignmentRestriction.setAssignmentRestrictionType(
                    AssignmentRestrictionType.DEADLINE);
            if (dto.deadline() != null)
                defaultAssignmentRestriction.setDeadline(dto.deadline());
        } else {
            defaultAssignmentRestriction.setAssignmentRestrictionType(
                    AssignmentRestrictionType.ATTEMPT_PER_N_MINUTES);
            if (dto.minutesForAttempt() != null)
                defaultAssignmentRestriction.setMinutesForAttempt(
                        dto.minutesForAttempt());
        }
    }
}
