package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.restriction.DefaultRestrictionDto;
import org.apiapplication.dto.restriction.RestrictionDto;
import org.apiapplication.dto.restriction.RestrictionTypeDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.entities.assignment.DefaultAssignmentRestriction;
import org.apiapplication.entities.assignment.Function;
import org.apiapplication.entities.assignment.UserAssignment;
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

    private final SessionService sessionService;
    private final PermissionService permissionService;

    public AssignmentRestrictionServiceImpl(DefaultAssignmentRestrictionRepository defaultAssignmentRestrictionRepository,
                                            FunctionRepository functionRepository,
                                            SubjectRepository subjectRepository,
                                            UniversityRepository universityRepository,
                                            SessionService sessionService,
                                            PermissionService permissionService,
                                            UserAssignmentRepository userAssignmentRepository) {
        this.defaultAssignmentRestrictionRepository = defaultAssignmentRestrictionRepository;
        this.functionRepository = functionRepository;
        this.subjectRepository = subjectRepository;
        this.universityRepository = universityRepository;
        this.userAssignmentRepository = userAssignmentRepository;

        this.sessionService = sessionService;
        this.permissionService = permissionService;
    }

    @Override
    public List<DefaultRestrictionDto> get(Integer functionId, Integer subjectId, Integer universityId) {
        if (functionId != null) {
            Function function = functionRepository.findById(functionId)
                    .orElseThrow(() -> new EntityWithIdNotFoundException(
                            EntityName.FUNCTION, String.valueOf(functionId)
                    ));

            if (!permissionService.userCanAccessFunction(sessionService.getCurrentUser(),
                    function)) {
                throw new PermissionException();
            }

            List<DefaultAssignmentRestriction> defaultAssignmentRestrictions = new ArrayList<>();
            defaultAssignmentRestrictions.add(getDefaultRestrictionForFunction(function));
            return defaultAssignmentRestrictions.stream()
                    .map(this::getDefaultAssignmentRestrictionDto)
                    .toList();
        } else if (subjectId != null) {
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new EntityWithIdNotFoundException(
                            EntityName.SUBJECT, String.valueOf(subjectId)
                    ));

            if (!permissionService.userCanAccessSubject(sessionService.getCurrentUser(),
                    subject)) {
                throw new PermissionException();
            }

            List<DefaultAssignmentRestriction> defaultAssignmentRestrictions = new ArrayList<>();
            defaultAssignmentRestrictions.add(getDefaultRestrictionForSubject(subject));
            return defaultAssignmentRestrictions.stream()
                    .map(this::getDefaultAssignmentRestrictionDto)
                    .toList();
        } else if (universityId != null) {
            University university = universityRepository.findById(universityId)
                    .orElseThrow(() -> new EntityWithIdNotFoundException(
                            EntityName.UNIVERSITY, String.valueOf(universityId)
                    ));

            if (!permissionService.userCanAccessUniversity(sessionService.getCurrentUser(),
                    university)) {
                throw new PermissionException();
            }

            List<DefaultAssignmentRestriction> defaultAssignmentRestrictions = new ArrayList<>();
            defaultAssignmentRestrictions.add(getDefaultRestrictionForUniversity(university));
            return defaultAssignmentRestrictions.stream()
                    .map(this::getDefaultAssignmentRestrictionDto)
                    .toList();
        } else {
            if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
                throw new PermissionException();
            }

            List<DefaultAssignmentRestriction> defaultAssignmentRestrictions =
                    defaultAssignmentRestrictionRepository.findAll();
            return defaultAssignmentRestrictions.stream()
                    .map(this::getDefaultAssignmentRestrictionDto)
                    .toList();
        }
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

        if (dto.functionId() != null) {
            function = functionRepository.findById(dto.functionId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FUNCTION,
                            String.valueOf(dto.functionId())));

            if (!permissionService.userCanAccessFunction(sessionService.getCurrentUser(),
                    function)) {
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

        if (university == null && subject == null && function == null) {
            return;
        }

        DefaultAssignmentRestriction defaultAssignmentRestriction =
                new DefaultAssignmentRestriction();
        defaultAssignmentRestriction.setFunction(function);
        defaultAssignmentRestriction.setSubject(subject);
        defaultAssignmentRestriction.setUniversity(university);

        Optional<DefaultAssignmentRestriction> existingRestriction =
                defaultAssignmentRestrictionRepository.findAll().stream()
                        .filter(dar ->
                                dar.getUniversity().getId().equals(dto.universityId()) &&
                                        dar.getSubject().getId().equals(dto.subjectId()) &&
                                        dar.getFunction().getId().equals(dto.functionId()))
                        .findFirst();

        if (existingRestriction.isPresent()) {
            defaultAssignmentRestriction = existingRestriction.get();
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

        if (dto.userAssignmentId() != null) {
            userAssignment = userAssignmentRepository.findById(dto.userAssignmentId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                            String.valueOf(dto.userAssignmentId())));

            if (!sessionService.isUserTeacher(sessionService.getCurrentUser()) ||
                    !permissionService.userCanAccessAssignment(sessionService.getCurrentUser(),
                            userAssignment)) {
                throw new PermissionException();
            }

            userAssignments.add(userAssignment);
        } else if (dto.functionId() != null) {
            function = functionRepository.findById(dto.functionId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FUNCTION,
                            String.valueOf(dto.functionId())));

            if (!sessionService.isUserTeacher(sessionService.getCurrentUser()) ||
                    !permissionService.userCanAccessFunction(sessionService.getCurrentUser(),
                            function)) {
                throw new PermissionException();
            }

            userAssignments.addAll(function.getUserAssignments());
        } else if (dto.subjectId() != null) {
            subject = subjectRepository.findById(dto.subjectId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.SUBJECT,
                            String.valueOf(dto.subjectId())));

            if (!sessionService.isUserTeacher(sessionService.getCurrentUser()) ||
                    !permissionService.userCanAccessSubject(sessionService.getCurrentUser(),
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

            if (!sessionService.isUserTeacher(sessionService.getCurrentUser()) ||
                    !permissionService.userCanAccessUniversity(sessionService.getCurrentUser(),
                            university)) {
                throw new PermissionException();
            }

            university.getSubjects().stream()
                    .flatMap(s -> s.getFunctions().stream())
                    .flatMap(f -> f.getUserAssignments().stream())
                    .forEach(userAssignments::add);
        }

        if (university == null && subject == null && function == null && userAssignment == null) {
            return;
        }

        for (UserAssignment ua : userAssignments) {
            setAssignmentRestriction(dto, ua);
        }

        userAssignmentRepository.saveAll(userAssignments);
    }

    @Override
    public void deleteDefaultRestriction(int defaultRestrictionId) {
        DefaultAssignmentRestriction defaultAssignmentRestriction =
                defaultAssignmentRestrictionRepository.findById(defaultRestrictionId)
                        .orElseThrow(() -> new EntityWithIdNotFoundException(
                                EntityName.DEFAULT_ASSIGNMENT_RESTRICTION,
                                String.valueOf(defaultRestrictionId)));

        if (defaultAssignmentRestriction.getFunction() != null) {
            if (!permissionService.userCanAccessFunction(sessionService.getCurrentUser(),
                    defaultAssignmentRestriction.getFunction())) {
                throw new PermissionException();
            }
        } else if (defaultAssignmentRestriction.getSubject() != null) {
            if (!permissionService.userCanAccessSubject(sessionService.getCurrentUser(),
                    defaultAssignmentRestriction.getSubject())) {
                throw new PermissionException();
            }
        } else if (defaultAssignmentRestriction.getUniversity() != null) {
            if (!permissionService.userCanAccessUniversity(sessionService.getCurrentUser(),
                    defaultAssignmentRestriction.getUniversity())) {
                throw new PermissionException();
            }
        }

        defaultAssignmentRestrictionRepository.delete(defaultAssignmentRestriction);
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
                defaultAssignmentRestriction.getUniversity().getId() != null ?
                        defaultAssignmentRestriction.getUniversity().getId() : null,
                defaultAssignmentRestriction.getAttemptsRemaining(),
                defaultAssignmentRestriction.getMinutesForAttempt(),
                defaultAssignmentRestriction.getDeadline()
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
