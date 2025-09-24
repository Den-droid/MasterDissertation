package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.answer.AnswerDto;
import org.apiapplication.dto.assignment.AssignmentAnswerDto;
import org.apiapplication.dto.assignment.AssignmentDto;
import org.apiapplication.dto.assignment.AssignmentResponseDto;
import org.apiapplication.dto.assignment.UserAssignmentDto;
import org.apiapplication.entities.Answer;
import org.apiapplication.entities.Assignment;
import org.apiapplication.entities.Function;
import org.apiapplication.entities.Mark;
import org.apiapplication.entities.user.User;
import org.apiapplication.enums.AssignmentStatus;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.repositories.*;
import org.apiapplication.repositories.*;
import org.apiapplication.services.interfaces.AssignmentService;
import org.apiapplication.utils.AnswerParser;
import org.apiapplication.utils.ExpressionParser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
                assignment.getFunction().getVariablesCount(),
                assignment.getStatus()
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
                            assignment.getAttemptsRemaining(),
                            assignment.getStatus(),
                            assignment.getFunction().getResultType(),
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

        List<Function> possibleFunctions = functionRepository.findAll();

        List<Assignment> assignments = user.getAssignments();
        List<Function> assignedFunctions = assignments.stream()
                .map(Assignment::getFunction)
                .toList();

        possibleFunctions = possibleFunctions.stream()
                .filter(f -> !assignedFunctions.contains(f))
                .toList();

        Random random = new Random();
        int possibleFunctionsCount = possibleFunctions.size();
        int possibleFunctionIndex = random.nextInt(possibleFunctionsCount);

        Assignment assignment = new Assignment();
        assignment.setUserAssigned(user);
        assignment.setFunction(possibleFunctions.get(possibleFunctionIndex));
        assignment.setAttemptsRemaining(5);
        assignment.setHasCorrectAnswer(false);
        assignment.setStatus(AssignmentStatus.ASSIGNED);

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

        String[] correctValues = assignment.getFunction().getCorrectValues().split("[;]");

        Answer answer = new Answer();

        for (String correctValue : correctValues) {
            if (Double.parseDouble(correctValue) == result) {
                answer.setCorrect(true);
                assignment.setHasCorrectAnswer(true);
                break;
            }
        }
        assignment.setAttemptsRemaining(assignment.getAttemptsRemaining() - 1);
        assignment.setLastAttemptTime(LocalDateTime.now());

        answer.setAssignment(assignment);
        answer.setAnswer(assignmentAnswerDto.answer());
        answer.setResult(result);

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
                .sorted(Comparator.comparingInt(Answer::getAnswerNumber).reversed())
                .map(a1 -> new AnswerDto(a1.getAnswerNumber(), a1.getAnswer(), a1.getResult(), a1.isCorrect()))
                .toList();

        return answerDtos;
    }
}
