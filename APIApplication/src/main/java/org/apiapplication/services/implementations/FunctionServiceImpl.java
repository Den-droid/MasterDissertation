package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.assignment.AssignmentFunctionDto;
import org.apiapplication.dto.common.IdDto;
import org.apiapplication.dto.function.AddFunctionDto;
import org.apiapplication.dto.function.FunctionDto;
import org.apiapplication.dto.function.UpdateFunctionDto;
import org.apiapplication.dto.subject.SubjectDto;
import org.apiapplication.dto.university.UniversityDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.entities.assignment.Function;
import org.apiapplication.entities.assignment.FunctionMinMaxValue;
import org.apiapplication.entities.assignment.UserAssignment;
import org.apiapplication.entities.user.User;
import org.apiapplication.entities.user.UserPermission;
import org.apiapplication.enums.FunctionResultType;
import org.apiapplication.exceptions.entity.EntityCantBeDeletedException;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.entity.EntityWithNameAlreadyFoundException;
import org.apiapplication.exceptions.function.FunctionSameExistsException;
import org.apiapplication.exceptions.function.FunctionTextFormatIncorrectException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.*;
import org.apiapplication.services.interfaces.FunctionService;
import org.apiapplication.services.interfaces.PermissionService;
import org.apiapplication.services.interfaces.SessionService;
import org.apiapplication.utils.ExpressionParser;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class FunctionServiceImpl implements FunctionService {
    private final FunctionRepository functionRepository;
    private final SubjectRepository subjectRepository;
    private final FunctionMinMaxValueRepository functionMinMaxValueRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final UserAssignmentRepository userAssignmentRepository;

    private final PermissionService permissionService;
    private final SessionService sessionService;

    public FunctionServiceImpl(FunctionRepository functionRepository,
                               SubjectRepository subjectRepository,
                               FunctionMinMaxValueRepository functionMinMaxValueRepository,
                               UserPermissionRepository userPermissionRepository,
                               UserAssignmentRepository userAssignmentRepository,
                               PermissionService permissionService,
                               SessionService sessionService) {
        this.functionRepository = functionRepository;
        this.subjectRepository = subjectRepository;
        this.functionMinMaxValueRepository = functionMinMaxValueRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.userAssignmentRepository = userAssignmentRepository;

        this.permissionService = permissionService;
        this.sessionService = sessionService;
    }

    @Override
    public FunctionDto getFunctionById(int id) {
        return getFunctionDtoFromFunction(List.of(
                functionRepository.findById(id).orElseThrow(
                        () -> new EntityWithIdNotFoundException(EntityName.FUNCTION, String.valueOf(id)))
        )).get(0);
    }

    @Override
    public List<AssignmentFunctionDto> getFunctionsByAssignmentIds(List<Integer> assignmentIds) {
        List<UserAssignment> userAssignments = userAssignmentRepository.findAllById(assignmentIds);
        List<Integer> functionIds = userAssignments.stream()
                .map(ua -> ua.getFunction().getId())
                .distinct().toList();
        List<Function> functions = functionRepository.findAllById(functionIds);

        List<AssignmentFunctionDto> assignmentFunctionDtos = new ArrayList<>();
        for (UserAssignment userAssignment : userAssignments) {
            int functionId = userAssignment.getFunction().getId();
            AssignmentFunctionDto assignmentFunctionDto =
                    new AssignmentFunctionDto(functionId, userAssignment.getId());
            assignmentFunctionDtos.add(assignmentFunctionDto);
        }

        return assignmentFunctionDtos;
    }

    @Override
    public List<FunctionDto> get(Integer subjectId) {
        List<Function> functions;
        if (subjectId != null) {
            Subject subject = subjectRepository.findById(subjectId).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.SUBJECT,
                            String.valueOf(subjectId))
            );

            if (!permissionService.userCanAccessSubject(sessionService.getCurrentUser(), subject)) {
                functions = getByUser(sessionService.getCurrentUser()).stream()
                        .filter(f -> f.getSubject().getId().equals(subjectId))
                        .toList();
            } else {
                functions = subject.getFunctions();
            }
        } else {
            functions = getByUser(sessionService.getCurrentUser()).stream().toList();
        }

        return getFunctionDtoFromFunction(functions);
    }

    @Override
    public IdDto add(AddFunctionDto dto) {
        Optional<Function> existingFunction = functionRepository.findAll().stream()
                .filter(u -> u.getText().equalsIgnoreCase(dto.text()))
                .findFirst();

        if (existingFunction.isPresent()) {
            throw new EntityWithNameAlreadyFoundException(EntityName.FUNCTION,
                    dto.text());
        }

        Subject subject = subjectRepository
                .findById(dto.subjectId())
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FUNCTION,
                        String.valueOf(dto.subjectId())));

        if (!permissionService.userCanAccessSubject(sessionService.getCurrentUser(),
                subject)) {
            throw new PermissionException();
        }

        checkFunctionText(dto.text(), dto.variablesCount());

        Function function = new Function();
        function.setText(dto.text());

        List<FunctionMinMaxValue> functionMinMaxValues = new ArrayList<>();
        for (double minValue : dto.minValues()) {
            FunctionMinMaxValue functionMinMaxValue = new FunctionMinMaxValue();
            functionMinMaxValue.setFunctionResultType(FunctionResultType.MIN);
            functionMinMaxValue.setValue(minValue);
            functionMinMaxValue.setFunction(function);

            functionMinMaxValues.add(functionMinMaxValue);
        }

        for (double maxValue : dto.maxValues()) {
            FunctionMinMaxValue functionMinMaxValue = new FunctionMinMaxValue();
            functionMinMaxValue.setFunctionResultType(FunctionResultType.MAX);
            functionMinMaxValue.setValue(maxValue);
            functionMinMaxValue.setFunction(function);

            functionMinMaxValues.add(functionMinMaxValue);
        }

        function.setFunctionMinMaxValues(functionMinMaxValues);
        function.setVariablesCount(dto.variablesCount());
        function.setSubject(subject);

        functionMinMaxValueRepository.saveAll(functionMinMaxValues);
        functionRepository.save(function);

        return new IdDto(function.getId());
    }

    @Override
    public void update(int id, UpdateFunctionDto dto) {
        Function existingFunction = functionRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FUNCTION,
                        String.valueOf(id)));

        if (!permissionService.userCanAccessFunction(sessionService.getCurrentUser(),
                existingFunction)) {
            throw new PermissionException();
        }

        if (dto.subjectId() != null) {
            Subject subject = subjectRepository
                    .findById(dto.subjectId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.SUBJECT,
                            String.valueOf(dto.subjectId())));

            if (!permissionService.userCanAccessSubject(sessionService.getCurrentUser(),
                    subject)) {
                throw new PermissionException();
            }

            existingFunction.setSubject(subject);
        }

        if (dto.text() != null && !dto.text().isEmpty()) {
            checkFunctionText(dto.text(), dto.variablesCount() != null ? dto.variablesCount()
                    : existingFunction.getVariablesCount());

            Optional<Function> functionWithSameText = functionRepository.findAll()
                    .stream()
                    .filter(f -> f.getText().equalsIgnoreCase(dto.text())
                            && !f.getId().equals(id))
                    .findFirst();

            if (functionWithSameText.isPresent()) {
                throw new FunctionSameExistsException();
            }

            existingFunction.setText(dto.text());
        }

        List<FunctionMinMaxValue> functionMinMaxValues = existingFunction.getFunctionMinMaxValues();
        List<FunctionMinMaxValue> minMaxValuesToAdd = new ArrayList<>();
        List<FunctionMinMaxValue> minMaxValuesToRemove = new ArrayList<>();

        if (!dto.minValues().isEmpty()) {
            List<Double> minValues = new ArrayList<>(
                    functionMinMaxValues.stream()
                            .filter(mv ->
                                    mv.getFunctionResultType().equals(FunctionResultType.MIN))
                            .map(FunctionMinMaxValue::getValue)
                            .toList());

            for (double minValue : dto.minValues()) {
                if (minValues.contains(minValue)) {
                    continue;
                }

                FunctionMinMaxValue functionMinMaxValue = new FunctionMinMaxValue();
                functionMinMaxValue.setFunctionResultType(FunctionResultType.MIN);
                functionMinMaxValue.setValue(minValue);
                functionMinMaxValue.setFunction(existingFunction);

                minMaxValuesToAdd.add(functionMinMaxValue);
                minValues.add(minValue);
            }

            List<FunctionMinMaxValue> functionMinValues = functionMinMaxValues.stream()
                    .filter(mv ->
                            mv.getFunctionResultType().equals(FunctionResultType.MIN))
                    .toList();

            for (FunctionMinMaxValue fmmv : functionMinValues) {
                if (!dto.minValues().contains(fmmv.getValue())) {
                    minMaxValuesToRemove.add(fmmv);
                }
            }
        } else {
            List<FunctionMinMaxValue> functionMaxValues = functionMinMaxValues.stream()
                    .filter(mv ->
                            mv.getFunctionResultType().equals(FunctionResultType.MIN))
                    .toList();

            minMaxValuesToRemove.addAll(functionMaxValues);
        }

        if (!dto.maxValues().isEmpty()) {
            List<Double> maxValues = new ArrayList<>(
                    functionMinMaxValues.stream()
                            .filter(mv ->
                                    mv.getFunctionResultType().equals(FunctionResultType.MAX))
                            .map(FunctionMinMaxValue::getValue)
                            .toList());

            for (double maxValue : dto.maxValues()) {
                if (maxValues.contains(maxValue)) {
                    continue;
                }

                FunctionMinMaxValue functionMinMaxValue = new FunctionMinMaxValue();
                functionMinMaxValue.setFunctionResultType(FunctionResultType.MAX);
                functionMinMaxValue.setValue(maxValue);
                functionMinMaxValue.setFunction(existingFunction);

                minMaxValuesToAdd.add(functionMinMaxValue);
                maxValues.add(maxValue);
            }

            List<FunctionMinMaxValue> functionMaxValues = functionMinMaxValues.stream()
                    .filter(mv ->
                            mv.getFunctionResultType().equals(FunctionResultType.MAX))
                    .toList();

            for (FunctionMinMaxValue fmmv : functionMaxValues) {
                if (!dto.maxValues().contains(fmmv.getValue())) {
                    minMaxValuesToRemove.add(fmmv);
                }
            }
        } else {
            List<FunctionMinMaxValue> functionMaxValues = functionMinMaxValues.stream()
                    .filter(mv ->
                            mv.getFunctionResultType().equals(FunctionResultType.MAX))
                    .toList();

            minMaxValuesToRemove.addAll(functionMaxValues);
        }

        if (dto.variablesCount() != null) {
            existingFunction.setVariablesCount(dto.variablesCount());
        }

        functionMinMaxValueRepository.deleteAll(minMaxValuesToRemove);
        functionMinMaxValueRepository.saveAll(minMaxValuesToAdd);
        functionRepository.save(existingFunction);
    }

    @Override
    public void delete(int functionId) {
        Function function = functionRepository.findById(functionId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(
                        EntityName.FUNCTION, String.valueOf(functionId)
                ));

        if (!permissionService.userCanAccessFunction(sessionService.getCurrentUser(),
                function)) {
            throw new PermissionException();
        }

        if (!function.getUserAssignments().isEmpty()) {
            throw new EntityCantBeDeletedException();
        }

        userPermissionRepository.deleteAll(function.getUserPermissions());
        functionRepository.delete(function);
    }

    private List<FunctionDto> getFunctionDtoFromFunction(List<Function> functions) {
        return functions.stream()
                .map(f -> {
                    Subject subject = f.getSubject();
                    University university = subject.getUniversity();
                    UniversityDto universityDto = new UniversityDto(university.getId(), university.getName());
                    SubjectDto subjectDto = new SubjectDto(subject.getId(), subject.getName(), universityDto);

                    return new FunctionDto(f.getId(), f.getText(), f.getVariablesCount(),
                            getMinMaxValuesForFunction(f, FunctionResultType.MIN),
                            getMinMaxValuesForFunction(f, FunctionResultType.MAX),
                            subjectDto);
                }).toList();
    }

    private List<Double> getMinMaxValuesForFunction(Function function,
                                                    FunctionResultType functionResultType) {
        return function.getFunctionMinMaxValues().stream()
                .filter(fmmv -> fmmv.getFunctionResultType() == functionResultType)
                .map(FunctionMinMaxValue::getValue)
                .toList();
    }

    private Set<Function> getByUser(User user) {
        Set<Function> functions = new HashSet<>();
        List<UserPermission> userPermissions = user.getUserPermissions();

        for (UserPermission userPermission : userPermissions) {
            if (userPermission.getFunction() != null) {
                functions.add(userPermission.getFunction());
            } else if (userPermission.getSubject() != null) {
                functions.addAll(
                        userPermission.getSubject().getFunctions().stream()
                                .toList()
                );
            } else if (userPermission.getUniversity() != null) {
                functions.addAll(
                        userPermission.getUniversity().getSubjects().stream()
                                .flatMap(s -> s.getFunctions().stream())
                                .toList()
                );
            }
        }
        return functions;
    }

    private void checkFunctionText(String text, int variablesCount) {
        try {
            Map<String, Double> answerVariables = new HashMap<>();
            for (int i = 0; i < variablesCount; i++) {
                answerVariables.put("x" + (i + 1), 0D);
            }
            ExpressionParser.parse(text, answerVariables);
        } catch (RuntimeException e) {
            throw new FunctionTextFormatIncorrectException();
        }
    }
}
