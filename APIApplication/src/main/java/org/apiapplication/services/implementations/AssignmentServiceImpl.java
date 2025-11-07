package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.answer.AnswerDto;
import org.apiapplication.dto.assignment.*;
import org.apiapplication.dto.university.UniversityDto;
import org.apiapplication.dto.user.UserDto;
import org.apiapplication.entities.assignment.*;
import org.apiapplication.entities.user.Role;
import org.apiapplication.entities.user.User;
import org.apiapplication.entities.user.UserPermission;
import org.apiapplication.enums.AssignmentRestrictionType;
import org.apiapplication.enums.AssignmentStatus;
import org.apiapplication.enums.FunctionResultType;
import org.apiapplication.enums.UserRole;
import org.apiapplication.exceptions.assignment.*;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.*;
import org.apiapplication.services.interfaces.AssignmentRestrictionService;
import org.apiapplication.services.interfaces.AssignmentService;
import org.apiapplication.services.interfaces.PermissionService;
import org.apiapplication.services.interfaces.SessionService;
import org.apiapplication.utils.AnswerParser;
import org.apiapplication.utils.ExpressionParser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class AssignmentServiceImpl implements AssignmentService {
    private final UserRepository userRepository;
    private final FunctionRepository functionRepository;
    private final UserAssignmentRepository userAssignmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final AnswerRepository answerRepository;

    private final PermissionService permissionService;
    private final AssignmentRestrictionService assignmentRestrictionService;
    private final SessionService sessionService;

    public AssignmentServiceImpl(UserRepository userRepository,
                                 FunctionRepository functionRepository,
                                 UserAssignmentRepository userAssignmentRepository,
                                 AssignmentRepository assignmentRepository,
                                 AnswerRepository answerRepository,
                                 PermissionService permissionService,
                                 AssignmentRestrictionService
                                         assignmentRestrictionService,
                                 SessionService sessionService) {
        this.userRepository = userRepository;
        this.functionRepository = functionRepository;
        this.userAssignmentRepository = userAssignmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.answerRepository = answerRepository;

        this.permissionService = permissionService;
        this.assignmentRestrictionService = assignmentRestrictionService;
        this.sessionService = sessionService;
    }

    @Override
    public AssignmentDto getById(int userAssignmentId) {
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                        String.valueOf(userAssignmentId))
        );

        if (!permissionService.userCanAccessAssignment(sessionService.getCurrentUser(),
                userAssignment)) {
            throw new PermissionException();
        }

        return new AssignmentDto(
                userAssignment.getAssignment().getText(),
                userAssignment.getFunction().getVariablesCount(),
                userAssignment.getStatus().ordinal(),
                userAssignment.getRestrictionType().ordinal(),
                userAssignment.getAttemptsRemaining(),
                userAssignment.getDeadline(),
                userAssignment.getLastAttemptTime() != null ?
                        userAssignment.getLastAttemptTime()
                                .plusMinutes(userAssignment.getMinutesForAttempt())
                        : LocalDateTime.now()
        );
    }

    @Override
    public List<UserAssignmentDto> get() {
        List<UserAssignment> assignments;
        User user = sessionService.getCurrentUser();

        Role role = user.getRoles().get(0);

        if (role.getName().equals(UserRole.STUDENT)) {
            assignments = user.getUserAssignments();
        } else {
            assignments = getByUser(user).stream().toList();
        }

        List<UserAssignmentDto> userAssignmentDtos = assignments.stream()
                .map(userAssignment -> {
                    List<Mark> marks = userAssignment.getMarks();
                    Mark mark = null;
                    if (marks != null && !marks.isEmpty()) {
                        mark = marks.get(0);
                    }

                    Assignment assignment = userAssignment.getAssignment();

                    return new UserAssignmentDto(userAssignment.getId(),
                            assignment.getText(),
                            userAssignment.getStatus().ordinal(),
                            assignment.getFunctionResultType().ordinal(),
                            userAssignment.getRestrictionType().ordinal(),
                            userAssignment.getAttemptsRemaining(),
                            userAssignment.getDeadline(),
                            userAssignment.getLastAttemptTime() != null ?
                                    userAssignment.getLastAttemptTime()
                                            .plusMinutes(userAssignment.getMinutesForAttempt())
                                    : LocalDateTime.now(),
                            mark != null ? mark.getMark() : -1,
                            mark != null ? mark.getComment() : "",
                            getUserDto(userAssignment.getUser())
                    );
                })
                .toList();

        return userAssignmentDtos;
    }

    @Override
    public void assign(AssignDto dto) {
        if (!sessionService.isUserStudent(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        User user = userRepository.findById(dto.userId()).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER, String.valueOf(dto.userId()))
        );

        List<Function> allFunctions = functionRepository.findAll();
        List<Assignment> allAssignments = assignmentRepository.findAll();
        List<UserAssignment> possibleUserAssignments = new ArrayList<>();

        for (Function function : allFunctions) {
            for (Assignment assignment : allAssignments) {
                if (assignment.getFunctionResultType().equals(FunctionResultType.MIN) &&
                        !getMinMaxValuesForFunction(function, FunctionResultType.MIN).isEmpty())
                    possibleUserAssignments.add(new UserAssignment(user, function, assignment));
                else if (assignment.getFunctionResultType().equals(FunctionResultType.MAX) &&
                        !getMinMaxValuesForFunction(function, FunctionResultType.MAX).isEmpty()) {
                    possibleUserAssignments.add(new UserAssignment(user, function, assignment));
                }
            }
        }

        if (possibleUserAssignments.isEmpty())
            throw new NoAvailableAssignmentsException();

        List<UserAssignment> userAssignments = user.getUserAssignments();
        possibleUserAssignments.removeAll(userAssignments);

        Random random = new Random();
        int possibleFunctionsCount = possibleUserAssignments.size();
        int possibleFunctionIndex = random.nextInt(possibleFunctionsCount);

        UserAssignment userAssignment = new UserAssignment();
        userAssignment.setAssignment(possibleUserAssignments.get(possibleFunctionIndex).getAssignment());
        userAssignment.setUser(user);
        userAssignment.setFunction(possibleUserAssignments.get(possibleFunctionIndex).getFunction());
        userAssignment.setHasCorrectAnswer(false);
        userAssignment.setStatus(AssignmentStatus.ASSIGNED);

        DefaultAssignmentRestriction defaultAssignmentRestriction =
                assignmentRestrictionService.getDefaultRestrictionForFunction(userAssignment.getFunction());

        if (defaultAssignmentRestriction == null) {
            defaultAssignmentRestriction = assignmentRestrictionService.getDefaultRestriction();
        }

        if (defaultAssignmentRestriction.getAssignmentRestrictionType()
                .equals(AssignmentRestrictionType.N_ATTEMPTS)) {
            userAssignment.setRestrictionType(AssignmentRestrictionType.N_ATTEMPTS);
            userAssignment.setAttemptsRemaining(defaultAssignmentRestriction.getAttemptsRemaining());
        } else if (defaultAssignmentRestriction.getAssignmentRestrictionType()
                .equals(AssignmentRestrictionType.DEADLINE)) {
            userAssignment.setRestrictionType(AssignmentRestrictionType.DEADLINE);
            userAssignment.setDeadline(defaultAssignmentRestriction.getDeadline());
        } else {
            userAssignment.setRestrictionType(AssignmentRestrictionType.ATTEMPT_PER_N_MINUTES);
            userAssignment.setMinutesForAttempt(defaultAssignmentRestriction.getMinutesForAttempt());
        }

        userAssignmentRepository.save(userAssignment);
    }

    @Override
    public void startContinue(int userAssignmentId) {
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                        String.valueOf(userAssignmentId))
        );

        if (!sessionService.isUserStudent(sessionService.getCurrentUser()) ||
                !permissionService.userCanAccessAssignment(sessionService.getCurrentUser(),
                        userAssignment)) {
            throw new PermissionException();
        }

        userAssignment.setStatus(AssignmentStatus.ACTIVE);
        userAssignmentRepository.save(userAssignment);
    }

    @Override
    public void finish(int userAssignmentId) {
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                        String.valueOf(userAssignmentId))
        );

        if (!sessionService.isUserStudent(sessionService.getCurrentUser()) ||
                !permissionService.userCanAccessAssignment(sessionService.getCurrentUser(),
                        userAssignment)) {
            throw new PermissionException();
        }

        userAssignment.setStatus(AssignmentStatus.FINISHED);
        userAssignmentRepository.save(userAssignment);
    }

    @Override
    public AssignmentResponseDto answerAssignment(int userAssignmentId,
                                                  AssignmentAnswerDto dto) {
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                        String.valueOf(userAssignmentId)));

        if (!sessionService.isUserStudent(sessionService.getCurrentUser()) ||
                !permissionService.userCanAccessAssignment(sessionService.getCurrentUser(),
                        userAssignment)) {
            throw new PermissionException();
        }

        if (userAssignment.getRestrictionType().equals(AssignmentRestrictionType.N_ATTEMPTS) &&
                userAssignment.getAttemptsRemaining() == 0) {
            throw new AttemptsNotLeftException();
        } else if (userAssignment.getRestrictionType().equals(AssignmentRestrictionType.DEADLINE) &&
                userAssignment.getDeadline().isBefore(LocalDateTime.now())) {
            throw new AttemptAfterDeadlineException(userAssignment.getDeadline());
        } else if (userAssignment.getRestrictionType().equals(AssignmentRestrictionType.ATTEMPT_PER_N_MINUTES) &&
                (userAssignment.getLastAttemptTime() != null &&
                        userAssignment.getLastAttemptTime().plusMinutes(userAssignment.getMinutesForAttempt())
                                .isAfter(LocalDateTime.now()))
        ) {
            throw new AttemptLimitReachedException(userAssignment.getLastAttemptTime()
                    .plusMinutes(userAssignment.getMinutesForAttempt()));
        }

        if (userAssignment.isHasCorrectAnswer()) {
            throw new AlreadyCorrectAnswerException();
        }

        double result;
        try {
            Map<String, Double> answerVariables = AnswerParser.parseAnswer(dto.answer());
            result = ExpressionParser.parse(userAssignment.getFunction().getText(), answerVariables);
        } catch (RuntimeException e) {
            throw new AnswerFormatIncorrectException();
        }

        Function function = userAssignment.getFunction();
        FunctionResultType functionResultType = userAssignment.getAssignment().getFunctionResultType();

        List<Double> correctValues = getMinMaxValuesForFunction(function, functionResultType);

        Answer answer = new Answer();
        answer.setCorrect(false);
        userAssignment.setHasCorrectAnswer(false);

        for (Double correctValue : correctValues) {
            if (correctValue == result) {
                answer.setCorrect(true);
                userAssignment.setHasCorrectAnswer(true);
                break;
            }
        }

        if (userAssignment.getRestrictionType().equals(AssignmentRestrictionType.N_ATTEMPTS))
            userAssignment.setAttemptsRemaining(userAssignment.getAttemptsRemaining() - 1);
        else if (userAssignment.getRestrictionType()
                .equals(AssignmentRestrictionType.ATTEMPT_PER_N_MINUTES))
            userAssignment.setLastAttemptTime(LocalDateTime.now());

        answer.setUserAssignment(userAssignment);
        answer.setAnswer(dto.answer());
        answer.setResult(result);

        List<Answer> answers = userAssignment.getAnswers();
        answer.setAnswerNumber(answers.size() + 1);

        userAssignmentRepository.save(userAssignment);
        answerRepository.save(answer);

        return new AssignmentResponseDto(result, answer.isCorrect(),
                userAssignment.getRestrictionType().ordinal(),
                userAssignment.getAttemptsRemaining(),
                userAssignment.getDeadline(),
                LocalDateTime.now().plusMinutes(userAssignment.getMinutesForAttempt())
        );
    }

    @Override
    public List<AnswerDto> getAnswersForAssignment(int userAssignmentId) {
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                        String.valueOf(userAssignmentId)));

        if (sessionService.isUserAdmin(sessionService.getCurrentUser())
                || !permissionService.userCanAccessAssignment(sessionService.getCurrentUser(),
                userAssignment)) {
            throw new PermissionException();
        }

        List<Answer> answers = userAssignment.getAnswers();
        List<AnswerDto> answerDtos = answers.stream()
                .sorted(Comparator.comparingInt(Answer::getAnswerNumber).reversed())
                .map(a1 -> new AnswerDto(a1.getAnswerNumber(), a1.getAnswer(),
                        a1.getResult(), a1.isCorrect()))
                .toList();

        return answerDtos;
    }

    private Set<UserAssignment> getByUser(User user) {
        Set<UserAssignment> userAssignments = new HashSet<>();
        List<UserPermission> userPermissions = user.getUserPermissions();

        for (UserPermission userPermission : userPermissions) {
            if (userPermission.getUserAssignment() != null) {
                userAssignments.add(userPermission.getUserAssignment());
            } else if (userPermission.getFunction() != null) {
                userAssignments.addAll(userPermission.getFunction().getUserAssignments());
            } else if (userPermission.getSubject() != null) {
                userAssignments.addAll(
                        userPermission.getSubject().getFunctions().stream()
                                .flatMap(f -> f.getUserAssignments().stream())
                                .toList()
                );
            } else if (userPermission.getUniversity() != null) {
                userAssignments.addAll(
                        userPermission.getUniversity().getSubjects().stream()
                                .flatMap(s -> s.getFunctions().stream())
                                .flatMap(f -> f.getUserAssignments().stream())
                                .toList()
                );
            }
        }
        return userAssignments;
    }

    private List<Double> getMinMaxValuesForFunction(Function function,
                                                    FunctionResultType functionResultType) {
        return function.getFunctionMinMaxValues().stream()
                .filter(fmmv -> fmmv.getFunctionResultType() == functionResultType)
                .map(FunctionMinMaxValue::getValue)
                .toList();
    }

    private UserDto getUserDto(User user) {
        return new UserDto(user.getId(), user.getUserInfo().getFirstName(),
                user.getUserInfo().getLastName(),
                user.getEmail(),
                user.getRoles().get(0).getName().name(),
                user.isApproved(),
                new UniversityDto(user.getUserInfo().getUniversity().getId(),
                        user.getUserInfo().getUniversity().getName()));
    }
}
