package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.answer.AnswerDto;
import org.apiapplication.dto.assignment.*;
import org.apiapplication.dto.mark.MarkDto;
import org.apiapplication.dto.restriction.RestrictionTypeDto;
import org.apiapplication.dto.university.UniversityDto;
import org.apiapplication.dto.user.UserDto;
import org.apiapplication.entities.Group;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.assignment.*;
import org.apiapplication.entities.function.Function;
import org.apiapplication.entities.function.FunctionMinMaxValue;
import org.apiapplication.entities.maze.Maze;
import org.apiapplication.entities.maze.MazePoint;
import org.apiapplication.entities.user.Role;
import org.apiapplication.entities.user.User;
import org.apiapplication.entities.user.UserPermission;
import org.apiapplication.enums.*;
import org.apiapplication.exceptions.assignment.*;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.entity.EntityWithNameNotFoundException;
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
    private final UserAssignmentRepository userAssignmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final AnswerRepository answerRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;

    private final PermissionService permissionService;
    private final AssignmentRestrictionService assignmentRestrictionService;
    private final SessionService sessionService;

    public AssignmentServiceImpl(UserAssignmentRepository userAssignmentRepository,
                                 AssignmentRepository assignmentRepository,
                                 AnswerRepository answerRepository,
                                 SubjectRepository subjectRepository, GroupRepository groupRepository,
                                 PermissionService permissionService,
                                 AssignmentRestrictionService
                                         assignmentRestrictionService,
                                 SessionService sessionService) {
        this.userAssignmentRepository = userAssignmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.answerRepository = answerRepository;
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;

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
                getAssignmentStatusDto(userAssignment.getStatus()),
                getRestrictionTypeDto(userAssignment.getRestrictionType()),
                userAssignment.getAttemptsRemaining(),
                userAssignment.getDeadline(),
                userAssignment.getMinutesToDo()
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
                            getAssignmentStatusDto(userAssignment.getStatus()),
                            getRestrictionTypeDto(userAssignment.getRestrictionType()),
                            getAssignmentTypeDto(userAssignment.getAssignment().getAssignmentType()),
                            userAssignment.getAttemptsRemaining(),
                            userAssignment.getDeadline(),
                            userAssignment.getMinutesToDo(),
                            mark != null ? new MarkDto(mark.getId(), mark.getMark(), mark.getComment())
                                    : null,
                            getUserDto(userAssignment.getUser())
                    );
                })
                .toList();

        return userAssignmentDtos;
    }

    @Override
    public void assignFunction(AssignFunctionDto dto) {
        User user = sessionService.getCurrentUser();

        if (!sessionService.isUserStudent(user)) {
            throw new PermissionException();
        }

        boolean allPermittedSubjects = subjectRepository.findAll().stream()
                .filter(s -> dto.subjectIds().contains(s.getId()))
                .map(Subject::getUniversity)
                .allMatch(u -> user.getUserInfo().getUniversity().equals(u));

        if (!allPermittedSubjects) {
            throw new PermissionException();
        }

        List<Subject> subjects = subjectRepository.findAll().stream()
                .filter(s -> dto.subjectIds().contains(s.getId()))
                .toList();

        assignFunction(List.of(user), subjects);
    }

    @Override
    public void assignFunctionToGroup(AssignGroupDto dto) {
        User currentUser = sessionService.getCurrentUser();

        Group group = groupRepository.findById(dto.groupId())
                .orElseThrow(() -> new EntityWithIdNotFoundException(
                        EntityName.GROUP, String.valueOf(dto.groupId())
                ));

        if (!group.getOwner().getId().equals(currentUser.getId()))
            throw new PermissionException();

        List<Subject> subjects = group.getSubjects().stream().toList();

        assignFunction(group.getStudents().stream().toList(), subjects);
    }

    @Override
    public void assignMaze() {
        User user = sessionService.getCurrentUser();

        if (!sessionService.isUserStudent(user)) {
            throw new PermissionException();
        }

        assignMaze(List.of(user));
    }

    @Override
    public void assignMazeToGroup(AssignGroupDto assignGroupDto) {
        User currentUser = sessionService.getCurrentUser();

        Group group = groupRepository.findById(assignGroupDto.groupId())
                .orElseThrow(() -> new EntityWithIdNotFoundException(
                        EntityName.GROUP, String.valueOf(assignGroupDto.groupId())
                ));

        if (!group.getOwner().getId().equals(currentUser.getId()))
            throw new PermissionException();

        assignMaze(group.getStudents().stream().toList());
    }

    private void assignFunction(List<User> users, List<Subject> subjects) {
        List<UserAssignment> allUserAssignments = new ArrayList<>();

        List<Function> allFunctions = subjects.stream()
                .flatMap(f -> f.getFunctions().stream())
                .toList();
        List<Assignment> allAssignments = assignmentRepository.findAll();

        for (User user : users) {
            List<UserAssignment> possibleUserAssignments = new ArrayList<>();

            for (Function function : allFunctions) {
                for (Assignment assignment : allAssignments) {
                    if (assignment.getAssignmentType().equals(AssignmentType.FUNCTION_MIN) &&
                            !getMinMaxValuesForFunction(function, FunctionResultType.MIN).isEmpty())
                        possibleUserAssignments.add(new UserAssignment(user, function, assignment));
                    else if (assignment.getAssignmentType().equals(AssignmentType.FUNCTION_MAX) &&
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

            updateRestrictionOnUserAssignmentCreation(userAssignment);

            allUserAssignments.add(userAssignment);
        }

        userAssignmentRepository.saveAll(allUserAssignments);
    }

    private void assignMaze(List<User> users) {
        List<UserAssignment> allUserAssignments = new ArrayList<>();
        Assignment assignment = assignmentRepository.findAll().stream()
                .filter(a -> a.getAssignmentType().equals(AssignmentType.MAZE))
                .findFirst().orElseThrow(NoAvailableAssignmentsException::new);
        List<Maze> possibleMazes = users.get(0).getUserInfo()
                .getUniversity().getMazes();

        for (User user : users) {
            List<UserAssignment> possibleUserAssignments = new ArrayList<>
                    (possibleMazes.stream()
                            .map(m -> new UserAssignment(user, m, assignment))
                            .toList());

            List<UserAssignment> currentUserAssignments = user.getUserAssignments();
            possibleUserAssignments.removeAll(currentUserAssignments);

            if (possibleUserAssignments.isEmpty())
                throw new NoAvailableAssignmentsException();

            Random random = new Random();
            int possibleFunctionsCount = possibleUserAssignments.size();
            int possibleFunctionIndex = random.nextInt(possibleFunctionsCount);

            UserAssignment userAssignment = new UserAssignment();
            userAssignment.setAssignment(possibleUserAssignments.get(possibleFunctionIndex).getAssignment());
            userAssignment.setUser(user);
            userAssignment.setMaze(possibleUserAssignments.get(possibleFunctionIndex).getMaze());
            userAssignment.setHasCorrectAnswer(false);
            userAssignment.setStatus(AssignmentStatus.ASSIGNED);

            updateRestrictionOnUserAssignmentCreation(userAssignment);

            allUserAssignments.add(userAssignment);
        }

        userAssignmentRepository.saveAll(allUserAssignments);
    }

    @Override
    public void start(int userAssignmentId) {
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

        if (userAssignment.getRestrictionType().equals(AssignmentRestrictionType.N_MINUTES)) {
            userAssignment.setDeadline(LocalDateTime.now().plusMinutes(userAssignment.getMinutesToDo()));
        }

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
    public AssignmentResponseDto answer(int userAssignmentId, AssignmentAnswerDto assignmentAnswerDto) {
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                        String.valueOf(userAssignmentId)));

        if (userAssignment.getFunction() != null) {
            return answerFunction(userAssignment, assignmentAnswerDto);
        } else if (userAssignment.getMaze() != null) {
            return answerMaze(userAssignment, assignmentAnswerDto);
        }

        return null;
    }

    private AssignmentResponseDto answerMaze(UserAssignment userAssignment, AssignmentAnswerDto assignmentAnswerDto) {
        checkPermissions(userAssignment);
        checkRestrictionsAfterAnswer(userAssignment);
        checkHasCorrectAnswer(userAssignment);

        Answer lastAnswer = userAssignment.getAnswers()
                .stream().max(Comparator.comparingInt(Answer::getAnswerNumber)).orElse(null);

        Answer currentAnswer = new Answer();
        currentAnswer.setCorrect(false);
        userAssignment.setHasCorrectAnswer(false);
        currentAnswer.setUserAssignment(userAssignment);
        currentAnswer.setWall(false);

        Maze maze = userAssignment.getMaze();
        MazePoint currentUserLocation = new MazePoint();

        if (lastAnswer == null) {
            MazePoint startPoint = maze.getMazePoints()
                    .stream()
                    .filter(mp -> mp.getMazePointType().equals(MazePointType.START))
                    .findFirst().orElseThrow(() -> new EntityWithNameNotFoundException(
                            EntityName.MAZE_POINT, "Старт"
                    ));

            currentUserLocation.setX(startPoint.getX());
            currentUserLocation.setY(startPoint.getY());
        } else {
            Map<String, Double> lastAnswerString = AnswerParser.parseAnswer(lastAnswer.getResult());
            currentUserLocation.setX(lastAnswerString.get("x").intValue());
            currentUserLocation.setY(lastAnswerString.get("y").intValue());
        }

        int userNextMoveInt;
        try {
            userNextMoveInt = Integer.parseInt(assignmentAnswerDto.answer());
        } catch (RuntimeException e) {
            userNextMoveInt = Arrays.stream(Direction.values())
                    .filter(d -> d.name().equalsIgnoreCase(assignmentAnswerDto.answer()))
                    .findFirst()
                    .orElseThrow(AnswerFormatIncorrectException::new)
                    .ordinal();
        }

        Direction userNextMove;

        try {
            userNextMove = Direction.values()[userNextMoveInt];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new AnswerFormatIncorrectException();
        }

        switch (userNextMove) {
            case LEFT -> currentUserLocation.setX(currentUserLocation.getX() - 1);
            case RIGHT -> currentUserLocation.setX(currentUserLocation.getX() + 1);
            case UP -> currentUserLocation.setY(currentUserLocation.getY() + 1);
            case DOWN -> currentUserLocation.setY(currentUserLocation.getY() - 1);
        }

        MazePoint finalPoint = maze.getMazePoints()
                .stream()
                .filter(mp -> mp.getMazePointType().equals(MazePointType.END))
                .findFirst().orElseThrow(() -> new EntityWithNameNotFoundException(
                        EntityName.MAZE_POINT, "Кінець"
                ));

        if (currentUserLocation.getX() == finalPoint.getX()
                && currentUserLocation.getY() == finalPoint.getY()) {
            currentAnswer.setCorrect(true);
            userAssignment.setHasCorrectAnswer(true);
        } else if (currentUserLocation.getX() == maze.getWidth() ||
                currentUserLocation.getX() == -1 ||
                currentUserLocation.getY() == maze.getHeight() ||
                currentUserLocation.getY() == -1) {
            currentAnswer.setWall(true);
        } else {
            List<MazePoint> mazeWalls = maze.getMazePoints()
                    .stream()
                    .filter(mp -> mp.getMazePointType().equals(MazePointType.WALL))
                    .toList();

            for (MazePoint mazePoint : mazeWalls) {
                if (currentUserLocation.getX() == mazePoint.getX() &&
                        currentUserLocation.getY() == mazePoint.getY()) {
                    currentAnswer.setWall(true);
                    break;
                }
            }
        }

        if (currentAnswer.isWall()) {
            switch (userNextMove) {
                case LEFT -> currentUserLocation.setX(currentUserLocation.getX() + 1);
                case RIGHT -> currentUserLocation.setX(currentUserLocation.getX() - 1);
                case UP -> currentUserLocation.setY(currentUserLocation.getY() - 1);
                case DOWN -> currentUserLocation.setY(currentUserLocation.getY() + 1);
            }
        }

        updateRestrictionAfterAnswer(userAssignment);

        currentAnswer.setAnswer(assignmentAnswerDto.answer());
        currentAnswer.setResult(currentUserLocation.toString());
        currentAnswer.setAnswerNumber(lastAnswer != null ? lastAnswer.getAnswerNumber() + 1 : 1);

        userAssignmentRepository.save(userAssignment);
        answerRepository.save(currentAnswer);

        return new AssignmentResponseDto(currentAnswer.getResult(), currentAnswer.isWall(),
                currentAnswer.isCorrect(),
                getRestrictionTypeDto(userAssignment.getRestrictionType()),
                userAssignment.getAttemptsRemaining(),
                userAssignment.getDeadline(),
                userAssignment.getMinutesToDo()
        );
    }

    private AssignmentResponseDto answerFunction(UserAssignment userAssignment,
                                                 AssignmentAnswerDto dto) {
        checkPermissions(userAssignment);
        checkRestrictionsAfterAnswer(userAssignment);
        checkHasCorrectAnswer(userAssignment);

        double result;
        try {
            Map<String, Double> answerVariables = AnswerParser.parseAnswer(dto.answer());
            result = ExpressionParser.parse(userAssignment.getFunction().getText(), answerVariables);
        } catch (RuntimeException e) {
            throw new AnswerFormatIncorrectException();
        }

        Function function = userAssignment.getFunction();
        FunctionResultType functionResultType = userAssignment.getAssignment()
                .getAssignmentType().equals(AssignmentType.FUNCTION_MIN) ? FunctionResultType.MIN
                : FunctionResultType.MAX;

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

        updateRestrictionAfterAnswer(userAssignment);

        answer.setUserAssignment(userAssignment);
        answer.setAnswer(dto.answer());
        answer.setResult(String.valueOf(result));

        List<Answer> answers = userAssignment.getAnswers();
        answer.setAnswerNumber(answers.size() + 1);

        userAssignmentRepository.save(userAssignment);
        answerRepository.save(answer);

        return new AssignmentResponseDto(answer.getResult(), false, answer.isCorrect(),
                getRestrictionTypeDto(userAssignment.getRestrictionType()),
                userAssignment.getAttemptsRemaining(),
                userAssignment.getDeadline(),
                userAssignment.getMinutesToDo()
        );
    }

    @Override
    public List<AnswerDto> getAnswersForAssignment(int userAssignmentId) {
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                        String.valueOf(userAssignmentId)));

        if (!permissionService.userCanAccessAssignment(sessionService.getCurrentUser(),
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
            } else if (userPermission.getMaze() != null) {
                userAssignments.addAll(userPermission.getMaze().getUserAssignments());
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
                userAssignments.addAll(
                        userPermission.getUniversity().getMazes().stream()
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

    private void checkRestrictionsAfterAnswer(UserAssignment userAssignment) {
        if (userAssignment.getRestrictionType().equals(AssignmentRestrictionType.N_ATTEMPTS) &&
                userAssignment.getAttemptsRemaining() == 0) {
            throw new AttemptsNotLeftException();
        } else {
            if (userAssignment.getDeadline().isBefore(LocalDateTime.now()))
                throw new AttemptAfterDeadlineException(userAssignment.getDeadline());
        }
    }

    private void checkPermissions(UserAssignment userAssignment) {
        if (!sessionService.isUserStudent(sessionService.getCurrentUser()) ||
                !permissionService.userCanAccessAssignment(sessionService.getCurrentUser(),
                        userAssignment)) {
            throw new PermissionException();
        }
    }

    private void checkHasCorrectAnswer(UserAssignment userAssignment) {
        if (userAssignment.isHasCorrectAnswer()) {
            throw new AlreadyCorrectAnswerException();
        }
    }

    private void updateRestrictionAfterAnswer(UserAssignment userAssignment) {
        if (userAssignment.getRestrictionType().equals(AssignmentRestrictionType.N_ATTEMPTS))
            userAssignment.setAttemptsRemaining(userAssignment.getAttemptsRemaining() - 1);
    }

    private void updateRestrictionOnUserAssignmentCreation(
            UserAssignment userAssignment) {
        Assignment assignment = userAssignment.getAssignment();

        DefaultAssignmentRestriction defaultAssignmentRestriction =
                assignment.getAssignmentType().equals(AssignmentType.MAZE) ?
                        assignmentRestrictionService
                                .getDefaultRestrictionForMaze(userAssignment.getMaze()) :
                        assignmentRestrictionService
                                .getDefaultRestrictionForFunction(userAssignment.getFunction());

        if (defaultAssignmentRestriction.getAssignmentRestrictionType()
                .equals(AssignmentRestrictionType.N_ATTEMPTS)) {
            userAssignment.setRestrictionType(AssignmentRestrictionType.N_ATTEMPTS);
            userAssignment.setAttemptsRemaining(defaultAssignmentRestriction.getAttemptsRemaining());
        } else if (defaultAssignmentRestriction.getAssignmentRestrictionType()
                .equals(AssignmentRestrictionType.DEADLINE)) {
            userAssignment.setRestrictionType(AssignmentRestrictionType.DEADLINE);
            userAssignment.setDeadline(defaultAssignmentRestriction.getDeadline());
        } else {
            userAssignment.setRestrictionType(AssignmentRestrictionType.N_MINUTES);
            userAssignment.setMinutesToDo(defaultAssignmentRestriction.getMinutesToDo());
        }
    }

    private AssignmentStatusDto getAssignmentStatusDto(AssignmentStatus assignmentStatus) {
        return new AssignmentStatusDto(assignmentStatus.ordinal(), assignmentStatus.name());
    }

    private RestrictionTypeDto getRestrictionTypeDto(AssignmentRestrictionType restrictionType) {
        return new RestrictionTypeDto(restrictionType.ordinal(), restrictionType.name());
    }

    private AssignmentTypeDto getAssignmentTypeDto(AssignmentType assignmentType) {
        return new AssignmentTypeDto(assignmentType.ordinal(), assignmentType.name());
    }
}
