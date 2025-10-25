package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.function.AddFunctionDto;
import org.apiapplication.dto.function.FunctionDto;
import org.apiapplication.dto.function.UpdateFunctionDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.assignment.Function;
import org.apiapplication.entities.user.User;
import org.apiapplication.entities.user.UserPermission;
import org.apiapplication.exceptions.entity.EntityCantBeDeletedException;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.entity.EntityWithNameAlreadyFoundException;
import org.apiapplication.exceptions.function.FunctionSameExistsException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.FunctionRepository;
import org.apiapplication.repositories.SubjectRepository;
import org.apiapplication.services.interfaces.FunctionService;
import org.apiapplication.services.interfaces.PermissionService;
import org.apiapplication.services.interfaces.SessionService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class FunctionServiceImpl implements FunctionService {
    private final FunctionRepository functionRepository;
    private final SubjectRepository subjectRepository;

    private final PermissionService permissionService;
    private final SessionService sessionService;

    public FunctionServiceImpl(FunctionRepository functionRepository,
                               SubjectRepository subjectRepository,
                               PermissionService permissionService,
                               SessionService sessionService) {
        this.functionRepository = functionRepository;
        this.subjectRepository = subjectRepository;

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
                .filter(u -> u.getText().equals(dto.text()))
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

        Function function = new Function();
        function.setText(dto.text());
        function.setMinValues(dto.minValues());
        function.setMaxValues(dto.maxValues());
        function.setVariablesCount(dto.variablesCount());
        function.setSubject(subject);

        functionRepository.save(function);

        return function.getId();
    }

    @Override
    public void update(UpdateFunctionDto dto) {
        Function existingFunction = functionRepository.findById(dto.id())
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.FUNCTION,
                        String.valueOf(dto.id())));

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

        Optional<Function> functionWithSameName = functionRepository.findAll()
                .stream()
                .filter(f -> f.getText().equalsIgnoreCase(dto.text())
                        && !f.getId().equals(dto.id()))
                .findFirst();

        if (functionWithSameName.isPresent()) {
            throw new FunctionSameExistsException();
        }

        existingFunction.setText(dto.text());
        existingFunction.setMinValues(dto.minValues());
        existingFunction.setMaxValues(dto.maxValues());
        existingFunction.setVariablesCount(dto.variablesCount());

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
                        f.getMinValues(), f.getMaxValues()))
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
}
