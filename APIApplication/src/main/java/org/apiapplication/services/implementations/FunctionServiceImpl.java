package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.function.AddFunctionDto;
import org.apiapplication.dto.function.FunctionDto;
import org.apiapplication.dto.function.UpdateFunctionDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.assignment.Function;
import org.apiapplication.entities.assignment.FunctionMinMaxValue;
import org.apiapplication.entities.user.User;
import org.apiapplication.entities.user.UserPermission;
import org.apiapplication.enums.FunctionResultType;
import org.apiapplication.exceptions.entity.EntityCantBeDeletedException;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.entity.EntityWithNameAlreadyFoundException;
import org.apiapplication.exceptions.function.FunctionSameExistsException;
import org.apiapplication.exceptions.function.FunctionTextFormatIncorrectException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.FunctionMinMaxValueRepository;
import org.apiapplication.repositories.FunctionRepository;
import org.apiapplication.repositories.SubjectRepository;
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

    private final PermissionService permissionService;
    private final SessionService sessionService;

    public FunctionServiceImpl(FunctionRepository functionRepository,
                               SubjectRepository subjectRepository,
                               FunctionMinMaxValueRepository functionMinMaxValueRepository,
                               PermissionService permissionService,
                               SessionService sessionService) {
        this.functionRepository = functionRepository;
        this.subjectRepository = subjectRepository;
        this.functionMinMaxValueRepository = functionMinMaxValueRepository;

        this.permissionService = permissionService;
        this.sessionService = sessionService;
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
    public int add(AddFunctionDto dto) {
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

        return function.getId();
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

        List<FunctionMinMaxValue> functionMinMaxValues = new ArrayList<>();

        if (!dto.minValues().isEmpty()) {
            for (double minValue : dto.minValues()) {
                FunctionMinMaxValue functionMinMaxValue = new FunctionMinMaxValue();
                functionMinMaxValue.setFunctionResultType(FunctionResultType.MIN);
                functionMinMaxValue.setValue(minValue);
                functionMinMaxValue.setFunction(existingFunction);

                functionMinMaxValues.add(functionMinMaxValue);
            }
        }
        if (!dto.maxValues().isEmpty()) {
            for (double maxValue : dto.maxValues()) {
                FunctionMinMaxValue functionMinMaxValue = new FunctionMinMaxValue();
                functionMinMaxValue.setFunctionResultType(FunctionResultType.MAX);
                functionMinMaxValue.setValue(maxValue);
                functionMinMaxValue.setFunction(existingFunction);

                functionMinMaxValues.add(functionMinMaxValue);
            }
        }

        existingFunction.setFunctionMinMaxValues(functionMinMaxValues);

        if (dto.variablesCount() != null) {
            existingFunction.setVariablesCount(dto.variablesCount());
        }

        functionMinMaxValueRepository.saveAll(functionMinMaxValues);
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

        functionRepository.delete(function);
    }

    private List<FunctionDto> getFunctionDtoFromFunction(List<Function> functions) {
        return functions.stream()
                .map(f -> new FunctionDto(f.getId(), f.getText(), f.getVariablesCount(),
                        getMinMaxValuesForFunction(f, FunctionResultType.MIN),
                        getMinMaxValuesForFunction(f, FunctionResultType.MAX)))
                .toList();
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
