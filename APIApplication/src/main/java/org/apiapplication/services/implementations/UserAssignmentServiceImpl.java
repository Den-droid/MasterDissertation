package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.answer.AnswerDto;
import org.apiapplication.dto.assignment.AssignmentAnswerDto;
import org.apiapplication.dto.assignment.AssignmentDto;
import org.apiapplication.dto.assignment.AssignmentResponseDto;
import org.apiapplication.dto.assignment.UserAssignmentDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.entities.assignment.*;
import org.apiapplication.entities.user.User;
import org.apiapplication.enums.AssignmentRestrictionType;
import org.apiapplication.enums.AssignmentStatus;
import org.apiapplication.enums.FunctionResultType;
import org.apiapplication.exceptions.assignment.*;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.repositories.*;
import org.apiapplication.services.interfaces.UserAssignmentService;
import org.apiapplication.utils.AnswerParser;
import org.apiapplication.utils.ExpressionParser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class UserAssignmentServiceImpl implements UserAssignmentService {
    UserRepository userRepository;
    FunctionRepository functionRepository;
    UserAssignmentRepository userAssignmentRepository;
    AssignmentRepository assignmentRepository;
    MarkRepository markRepository;
    AnswerRepository answerRepository;
    DefaultAssignmentRestrictionRepository defaultAssignmentRestrictionRepository;

    public UserAssignmentServiceImpl(AnswerRepository answerRepository,
                                     AssignmentRepository assignmentRepository,
                                     UserAssignmentRepository userAssignmentRepository,
                                     FunctionRepository functionRepository,
                                     MarkRepository markRepository,
                                     UserRepository userRepository,
                                     DefaultAssignmentRestrictionRepository defaultAssignmentRestrictionRepository) {
        this.answerRepository = answerRepository;
        this.assignmentRepository = assignmentRepository;
        this.userAssignmentRepository = userAssignmentRepository;
        this.functionRepository = functionRepository;
        this.markRepository = markRepository;
        this.userRepository = userRepository;
        this.defaultAssignmentRestrictionRepository = defaultAssignmentRestrictionRepository;
    }

    @Override
    public AssignmentDto getById(int userAssignmentId) {
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT, userAssignmentId)
        );

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
    public List<UserAssignmentDto> getByUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER, userId)
        );
        List<UserAssignment> assignments = user.getUserAssignments();

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
                            mark != null ? mark.getComment() : "");
                })
                .toList();

        return userAssignmentDtos;
    }

    @Override
    public void assign(int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER, userId)
        );

        List<Function> allFunctions = functionRepository.findAll();
        List<Assignment> allAssignments = assignmentRepository.findAll();
        List<UserAssignment> possibleUserAssignments = new ArrayList<>();

        for (Function function : allFunctions) {
            for (Assignment assignment : allAssignments) {
                if (assignment.getFunctionResultType().equals(FunctionResultType.MIN) &&
                        function.getMinValues() != null && !function.getMinValues().isEmpty())
                    possibleUserAssignments.add(new UserAssignment(user, function, assignment));
                else if (assignment.getFunctionResultType().equals(FunctionResultType.MAX) &&
                        function.getMaxValues() != null && !function.getMaxValues().isEmpty()) {
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

        DefaultAssignmentRestriction defaultAssignmentRestriction = getDefaultRestriction(possibleUserAssignments
                .get(possibleFunctionIndex).getFunction());

        if (defaultAssignmentRestriction != null) {
            if (defaultAssignmentRestriction.getAssignmentRestrictionType()
                    .equals(AssignmentRestrictionType.N_ATTEMPTS)) {
                userAssignment.setRestrictionType(AssignmentRestrictionType.N_ATTEMPTS);
                userAssignment.setAttemptsRemaining(defaultAssignmentRestriction.getAttemptsRemaining());
            } else if (defaultAssignmentRestriction.getAssignmentRestrictionType()
                    .equals(AssignmentRestrictionType.DEADLINE)) {
                userAssignment.setRestrictionType(AssignmentRestrictionType.DEADLINE);
                userAssignment.setDeadline(LocalDateTime.now()
                        .plusMinutes(defaultAssignmentRestriction.getMinutesToDeadline()));
            } else {
                userAssignment.setRestrictionType(AssignmentRestrictionType.ATTEMPT_PER_N_MINUTES);
                userAssignment.setAttemptsRemaining(defaultAssignmentRestriction.getAttemptsRemaining());
            }
        } else {
            userAssignment.setAttemptsRemaining(10);
            userAssignment.setRestrictionType(AssignmentRestrictionType.N_ATTEMPTS);
        }

        userAssignmentRepository.save(userAssignment);
    }

    @Override
    public void startContinue(int userAssignmentId) {
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT, userAssignmentId)
        );

        userAssignment.setStatus(AssignmentStatus.ACTIVE);
        userAssignmentRepository.save(userAssignment);
    }

    @Override
    public void finish(int userAssignmentId) {
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT, userAssignmentId)
        );

        userAssignment.setStatus(AssignmentStatus.FINISHED);
        userAssignmentRepository.save(userAssignment);
    }

    @Override
    public AssignmentResponseDto answerAssignment(int userAssignmentId,
                                                  AssignmentAnswerDto assignmentAnswerDto) {
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                        userAssignmentId));

        if (userAssignment.getRestrictionType().equals(AssignmentRestrictionType.N_ATTEMPTS) &&
                userAssignment.getAttemptsRemaining() == 0) {
            throw new AttemptsNotLeftException();
        } else if (userAssignment.getRestrictionType().equals(AssignmentRestrictionType.DEADLINE) &&
                userAssignment.getDeadline().isBefore(LocalDateTime.now())) {
            throw new AttemptAfterDeadlineException(userAssignment.getDeadline());
        } else if (userAssignment.getRestrictionType().equals(AssignmentRestrictionType.ATTEMPT_PER_N_MINUTES) &&
                userAssignment.getLastAttemptTime().plusMinutes(userAssignment.getMinutesForAttempt())
                        .isAfter(LocalDateTime.now())) {
            throw new AttemptLimitReachedException(userAssignment.getLastAttemptTime()
                    .plusMinutes(userAssignment.getMinutesForAttempt()));
        }

        if (userAssignment.isHasCorrectAnswer()) {
            throw new AlreadyCorrectAnswerException();
        }

        Map<String, Double> answerVariables = AnswerParser.parseAnswer(assignmentAnswerDto.answer());
        double result = ExpressionParser.parse(userAssignment.getFunction().getText(), answerVariables);

        Function function = userAssignment.getFunction();
        FunctionResultType functionResultType = userAssignment.getAssignment().getFunctionResultType();

        String[] correctValues = (functionResultType == FunctionResultType.MIN ?
                function.getMinValues() : function.getMaxValues())
                .split("[;]");

        Answer answer = new Answer();
        answer.setCorrect(false);
        userAssignment.setHasCorrectAnswer(false);

        for (String correctValue : correctValues) {
            if (Double.parseDouble(correctValue) == result) {
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
        answer.setAnswer(assignmentAnswerDto.answer());
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
        UserAssignment assignment = userAssignmentRepository.findById(userAssignmentId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                        userAssignmentId));

        List<Answer> answers = assignment.getAnswers();
        List<AnswerDto> answerDtos = answers.stream()
                .sorted(Comparator.comparingInt(Answer::getAnswerNumber).reversed())
                .map(a1 -> new AnswerDto(a1.getAnswerNumber(), a1.getAnswer(),
                        a1.getResult(), a1.isCorrect()))
                .toList();

        return answerDtos;
    }

    private DefaultAssignmentRestriction getDefaultRestriction(Function function) {
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

        defaultRestriction = defaultAssignmentRestrictions.stream()
                .filter(restriction -> restriction.getSubject() != null &&
                        Objects.equals(restriction.getSubject().getId(),
                                subject.getId()))
                .findFirst();

        if (defaultRestriction.isPresent()) {
            return defaultRestriction.get();
        }

        University university = function.getSubject().getUniversity();

        defaultRestriction = defaultAssignmentRestrictions.stream()
                .filter(restriction -> restriction.getUniversity() != null &&
                        Objects.equals(restriction.getUniversity().getId(),
                                university.getId()))
                .findFirst();

        if (defaultRestriction.isPresent()) {
            return defaultRestriction.get();
        }

        return null;
    }
}
