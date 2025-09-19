package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.dto.answer.AnswerDto;
import org.example.apiapplication.dto.assignment.AssignmentAnswerDto;
import org.example.apiapplication.dto.assignment.AssignmentDto;
import org.example.apiapplication.dto.assignment.AssignmentResponseDto;
import org.example.apiapplication.dto.assignment.UserAssignmentDto;
import org.example.apiapplication.entities.Answer;
import org.example.apiapplication.entities.Assignment;
import org.example.apiapplication.entities.Function;
import org.example.apiapplication.entities.Mark;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.enums.AssignmentStatus;
import org.example.apiapplication.enums.FunctionResultType;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.example.apiapplication.repositories.*;
import org.example.apiapplication.services.interfaces.AssignmentService;
import org.example.apiapplication.utils.AnswerParser;
import org.example.apiapplication.utils.ExpressionParser;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssignmentServiceImpl implements AssignmentService {
    UserRepository userRepository;
    FunctionRepository functionRepository;
    AssignmentRepository assignmentRepository;
    MarkRepository markRepository;
    AnswerRepository answerRepository;

    public AssignmentServiceImpl(AnswerRepository answerRepository,
                                 AssignmentRepository assignmentRepository,
                                 FunctionRepository functionRepository,
                                 MarkRepository markRepository,
                                 UserRepository userRepository) {
        this.answerRepository = answerRepository;
        this.assignmentRepository = assignmentRepository;
        this.functionRepository = functionRepository;
        this.markRepository = markRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AssignmentDto getById(int assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.ASSIGNMENT, assignmentId)
        );

        return new AssignmentDto(
                assignment.getFunction().getHint(),
                assignment.getAttemptsRemaining(),
                assignment.getFunction().getVariablesCount()
        );
    }

    @Override
    public List<UserAssignmentDto> getByUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER, userId)
        );
        List<Assignment> assignments = user.getAssignments();

        List<UserAssignmentDto> userAssignmentDtos = assignments.stream()
                .map(assignment -> {
                    List<Mark> marks = assignment.getMarks();
                    Mark mark = null;
                    if (marks != null && !marks.isEmpty()) {
                        mark = marks.get(0);
                    }

                    Function function = assignment.getFunction();

                    return new UserAssignmentDto(assignment.getId(),
                            function.getHint(),
                            function.getVariablesCount(),
                            assignment.getStatus().ordinal(),
                            assignment.getFunction().getResultType().ordinal(),
                            mark != null ? mark.getMark() : -1,
                            mark != null ? mark.getComment() : "");
                })
                .toList();

        return userAssignmentDtos;
    }

    @Override
    public boolean isAvailable(int userId) {
        return false;
    }

    @Override
    public void assign(int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER, userId)
        );

        List<Function> possibleFunctions = new ArrayList<>();

        for (Function function : functionRepository.findAll()) {
            possibleFunctions.add(function);
        }

        List<Assignment> assignments = user.getAssignments();
        List<Function> assignedFunctions = assignments.stream()
                .map(Assignment::getFunction)
                .toList();

        possibleFunctions = possibleFunctions.stream()
                .filter(f -> !assignedFunctions.contains(f))
                .collect(Collectors.toList());

        Random random = new Random();
        int possibleFunctionsCount = possibleFunctions.size();
        int possibleFunctionIndex = random.nextInt(possibleFunctionsCount);

        Assignment assignment = new Assignment();
        assignment.setUserAssigned(user);
        assignment.setFunction(possibleFunctions.get(possibleFunctionIndex));
        assignment.setAttemptsRemaining(25);
        assignment.setHasCorrectAnswer(false);
        assignment.setStatus(AssignmentStatus.ACTIVE);

        assignmentRepository.save(assignment);
    }

    @Override
    public void startContinue(int assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.ASSIGNMENT, assignmentId)
        );

        assignment.setStatus(AssignmentStatus.ACTIVE);
        assignmentRepository.save(assignment);
    }

    @Override
    public void stop(int assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.ASSIGNMENT, assignmentId)
        );

        if (assignment.hasCorrectAnswer())
            assignment.setStatus(AssignmentStatus.CORRECT_ANSWER_STOPPED);
        else
            assignment.setStatus(AssignmentStatus.STOPPED);

        assignmentRepository.save(assignment);
    }

    @Override
    public void finish(int assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.ASSIGNMENT, assignmentId)
        );

        assignment.setStatus(AssignmentStatus.FINISHED);
        assignmentRepository.save(assignment);
    }

    @Override
    public AssignmentResponseDto answerAssignment(int assignmentId, AssignmentAnswerDto assignmentAnswerDto) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.ASSIGNMENT,
                        assignmentId));

        Map<String, Double> answerVariables = AnswerParser.parseAnswer(assignmentAnswerDto.answer());
        double result = ExpressionParser.parse(assignment.getFunction().getText(), answerVariables);

        String correctResult = assignment.getFunction().getResultType()
                .equals(FunctionResultType.MIN)
                ? assignment.getFunction().getMinValue()
                : assignment.getFunction().getMaxValue();

        Answer answer = new Answer();

        if (Double.parseDouble(correctResult) == result) {
            answer.setCorrect(true);
            assignment.setHasCorrectAnswer(true);
        } else {
            answer.setCorrect(false);
            assignment.setAttemptsRemaining(assignment.getAttemptsRemaining() - 1);
            assignment.setHasCorrectAnswer(false);
        }

        answer.setAssignment(assignment);
        answer.setAnswer(assignmentAnswerDto.answer());

        List<Answer> answers = assignment.getAnswers();
        answer.setAnswerNumber(answers.size() + 1);

        assignmentRepository.save(assignment);
        answerRepository.save(answer);

        return new AssignmentResponseDto(result, assignment.getAttemptsRemaining(),
                answer.isCorrect());
    }

    @Override
    public List<AnswerDto> getAnswersForAssignment(int assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.ASSIGNMENT,
                        assignmentId));

        List<Answer> answers = assignment.getAnswers();
        List<AnswerDto> answerDtos = answers.stream()
                .sorted(Comparator.comparingInt(Answer::getAnswerNumber))
                .map(a1 -> new AnswerDto(a1.getAnswerNumber(), a1.getAnswer()))
                .toList();

        return answerDtos;
    }
}
