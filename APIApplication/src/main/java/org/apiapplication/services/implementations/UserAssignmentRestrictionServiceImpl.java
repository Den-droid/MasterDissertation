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
import org.apiapplication.services.interfaces.PermissionService;
import org.apiapplication.services.interfaces.SessionService;
import org.apiapplication.services.interfaces.UserAssignmentRestrictionService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class UserAssignmentRestrictionServiceImpl implements UserAssignmentRestrictionService {
    private final UserAssignmentRestrictionRepository userAssignmentRestrictionRepository;
    private final FunctionRepository functionRepository;
    private final SubjectRepository subjectRepository;
    private final UniversityRepository universityRepository;
    private final UserAssignmentRepository userAssignmentRepository;

    private final SessionService sessionService;
    private final PermissionService permissionService;

    public UserAssignmentRestrictionServiceImpl(UserAssignmentRestrictionRepository userAssignmentRestrictionRepository,
                                                FunctionRepository functionRepository,
                                                SubjectRepository subjectRepository,
                                                UniversityRepository universityRepository,
                                                SessionService sessionService,
                                                PermissionService permissionService,
                                                UserAssignmentRepository userAssignmentRepository) {
        this.userAssignmentRestrictionRepository = userAssignmentRestrictionRepository;
        this.functionRepository = functionRepository;
        this.subjectRepository = subjectRepository;
        this.universityRepository = universityRepository;
        this.userAssignmentRepository = userAssignmentRepository;

        this.sessionService = sessionService;
        this.permissionService = permissionService;
    }

    @Override
    public DefaultAssignmentRestriction getDefaultRestrictionForFunction(Function function) {
        List<DefaultAssignmentRestriction> defaultAssignmentRestrictions =
                userAssignmentRestrictionRepository.findAll();

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
                userAssignmentRestrictionRepository.findAll();

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
                userAssignmentRestrictionRepository.findAll();

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
    public void setDefaultRestriction(DefaultRestrictionDto defaultRestrictionDto) {
        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())
                && !sessionService.isUserTeacher(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        Function function = null;
        Subject subject = null;
        University university = null;

        if (defaultRestrictionDto.functionId() != null) {
            function = functionRepository.findById(defaultRestrictionDto.functionId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FUNCTION,
                            String.valueOf(defaultRestrictionDto.functionId())));

            if (!permissionService.userCanAccessFunction(sessionService.getCurrentUser(),
                    function)) {
                throw new PermissionException();
            }
        } else if (defaultRestrictionDto.subjectId() != null) {
            subject = subjectRepository.findById(defaultRestrictionDto.subjectId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.SUBJECT,
                            String.valueOf(defaultRestrictionDto.subjectId())));

            if (!permissionService.userCanAccessSubject(sessionService.getCurrentUser(),
                    subject)) {
                throw new PermissionException();
            }
        } else if (defaultRestrictionDto.universityId() != null) {
            university = universityRepository.findById(defaultRestrictionDto.universityId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                            String.valueOf(defaultRestrictionDto.universityId())));

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
                userAssignmentRestrictionRepository.findAll().stream()
                        .filter(dar ->
                                dar.getUniversity().getId().equals(defaultRestrictionDto.universityId()) &&
                                        dar.getSubject().getId().equals(defaultRestrictionDto.subjectId()) &&
                                        dar.getFunction().getId().equals(defaultRestrictionDto.functionId()))
                        .findFirst();

        if (existingRestriction.isPresent()) {
            defaultAssignmentRestriction = existingRestriction.get();
        }

        setAssignmentRestriction(defaultRestrictionDto, defaultAssignmentRestriction);

        userAssignmentRestrictionRepository.save(defaultAssignmentRestriction);
    }

    @Override
    public void setRestriction(RestrictionDto restrictionDto) {
        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())
                && !sessionService.isUserTeacher(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        List<UserAssignment> userAssignments = new ArrayList<>();

        UserAssignment userAssignment = null;
        Function function = null;
        Subject subject = null;
        University university = null;

        if (restrictionDto.userAssignmentId() != null) {
            userAssignment = userAssignmentRepository.findById(restrictionDto.userAssignmentId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                            String.valueOf(restrictionDto.userAssignmentId())));

            if (!permissionService.userCanAccessAssignment(sessionService.getCurrentUser(),
                    userAssignment)) {
                throw new PermissionException();
            }

            userAssignments.add(userAssignment);
        } else if (restrictionDto.functionId() != null) {
            function = functionRepository.findById(restrictionDto.functionId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FUNCTION,
                            String.valueOf(restrictionDto.functionId())));

            if (!permissionService.userCanAccessFunction(sessionService.getCurrentUser(),
                    function)) {
                throw new PermissionException();
            }

            userAssignments.addAll(function.getUserAssignments());
        } else if (restrictionDto.subjectId() != null) {
            subject = subjectRepository.findById(restrictionDto.subjectId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.SUBJECT,
                            String.valueOf(restrictionDto.subjectId())));

            if (!permissionService.userCanAccessSubject(sessionService.getCurrentUser(),
                    subject)) {
                throw new PermissionException();
            }

            subject.getFunctions().stream()
                    .flatMap(f -> f.getUserAssignments().stream())
                    .forEach(userAssignments::add);
        } else if (restrictionDto.universityId() != null) {
            university = universityRepository.findById(restrictionDto.universityId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                            String.valueOf(restrictionDto.universityId())));

            if (!permissionService.userCanAccessUniversity(sessionService.getCurrentUser(),
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
            setAssignmentRestriction(restrictionDto, ua);
        }

        userAssignmentRepository.saveAll(userAssignments);
    }

    @Override
    public void deleteDefaultRestriction(int defaultRestrictionId) {
        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())
                && !sessionService.isUserTeacher(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        userAssignmentRestrictionRepository.deleteById(defaultRestrictionId);
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

    private void setAssignmentRestriction(RestrictionDto restrictionDto, UserAssignment userAssignment) {
        userAssignment.setRestrictionType(
                Arrays.stream(AssignmentRestrictionType.values())
                        .filter(x -> restrictionDto.restrictionType()
                                == x.ordinal())
                        .findFirst().orElseThrow(() ->
                                new EntityWithIdNotFoundException(EntityName.RESTRICTION_TYPE,
                                        String.valueOf(restrictionDto.restrictionType())))
        );

        if (restrictionDto.restrictionType() == AssignmentRestrictionType.N_ATTEMPTS.ordinal()) {
            userAssignment.setRestrictionType(
                    AssignmentRestrictionType.N_ATTEMPTS);
            userAssignment.setAttemptsRemaining(
                    restrictionDto.attemptsRemaining());
        } else if (restrictionDto.restrictionType() == AssignmentRestrictionType.DEADLINE.ordinal()) {
            userAssignment.setRestrictionType(
                    AssignmentRestrictionType.DEADLINE);
            userAssignment.setDeadline(restrictionDto.deadline());
        } else {
            userAssignment.setRestrictionType(
                    AssignmentRestrictionType.ATTEMPT_PER_N_MINUTES);
            userAssignment.setMinutesForAttempt(
                    restrictionDto.minutesForAttempt());
        }
    }

    private void setAssignmentRestriction(DefaultRestrictionDto defaultRestrictionDto,
                                          DefaultAssignmentRestriction defaultAssignmentRestriction) {
        defaultAssignmentRestriction.setAssignmentRestrictionType(
                Arrays.stream(AssignmentRestrictionType.values())
                        .filter(x -> defaultRestrictionDto.restrictionType()
                                == x.ordinal())
                        .findFirst().orElseThrow(() ->
                                new EntityWithIdNotFoundException(EntityName.RESTRICTION_TYPE,
                                        String.valueOf(defaultRestrictionDto.restrictionType())))
        );

        if (defaultRestrictionDto.restrictionType() == AssignmentRestrictionType.N_ATTEMPTS.ordinal()) {
            defaultAssignmentRestriction.setAssignmentRestrictionType(
                    AssignmentRestrictionType.N_ATTEMPTS);
            defaultAssignmentRestriction.setAttemptsRemaining(
                    defaultRestrictionDto.attemptsRemaining());
        } else if (defaultRestrictionDto.restrictionType() == AssignmentRestrictionType.DEADLINE.ordinal()) {
            defaultAssignmentRestriction.setAssignmentRestrictionType(
                    AssignmentRestrictionType.DEADLINE);
            defaultAssignmentRestriction.setDeadline(defaultRestrictionDto.deadline());
        } else {
            defaultAssignmentRestriction.setAssignmentRestrictionType(
                    AssignmentRestrictionType.ATTEMPT_PER_N_MINUTES);
            defaultAssignmentRestriction.setMinutesForAttempt(
                    defaultRestrictionDto.minutesForAttempt());
        }
    }
}
