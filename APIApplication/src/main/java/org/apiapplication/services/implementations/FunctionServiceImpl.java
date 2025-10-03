package org.apiapplication.services.implementations;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.function.FunctionDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.assignment.Function;
import org.apiapplication.entities.user.User;
import org.apiapplication.entities.user.UserPermission;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.FunctionRepository;
import org.apiapplication.repositories.SubjectRepository;
import org.apiapplication.security.utils.SessionUtil;
import org.apiapplication.services.interfaces.FunctionService;
import org.apiapplication.services.interfaces.PermissionService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FunctionServiceImpl implements FunctionService {
    private FunctionRepository functionRepository;
    private SubjectRepository subjectRepository;

    private PermissionService permissionService;

    private SessionUtil sessionUtil;

    public FunctionServiceImpl(FunctionRepository functionRepository,
                               SubjectRepository subjectRepository,
                               PermissionService permissionService,
                               SessionUtil sessionUtil) {
        this.functionRepository = functionRepository;
        this.subjectRepository = subjectRepository;

        this.permissionService = permissionService;

        this.sessionUtil = sessionUtil;
    }

    @Override
    public List<FunctionDto> get(Integer subjectId) {
        List<Function> functions;
        if (subjectId != null) {
            Subject subject = subjectRepository.findById(subjectId).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.SUBJECT,
                            String.valueOf(subjectId))
            );

            if (!permissionService.userCanAccessSubject(sessionUtil.getUserFromSession(), subject)) {
                throw new PermissionException();
            }

            functions = subject.getFunctions();
        } else {
            functions = getFunctionsByUser(sessionUtil.getUserFromSession()).stream().toList();
        }

        return getFunctionDtoFromFunction(functions);
    }

    private List<FunctionDto> getFunctionDtoFromFunction(List<Function> functions) {
        return functions.stream()
                .map(f -> new FunctionDto(f.getId(), f.getText(), f.getVariablesCount(),
                        f.getMinValues(), f.getMaxValues()))
                .toList();
    }

    private Set<Function> getFunctionsByUser(User user) {
        Set<Function> functions = new HashSet<>();
        List<UserPermission> userPermissions = user.getUserPermissions();

        for (UserPermission userPermission : userPermissions) {
            if (userPermission.getFunction() != null) {
                functions.add(userPermission.getFunction());
            } else if (userPermission.getSubject() != null &&
                    userPermission.getUniversity() != null) {
                functions.addAll(
                        userPermission.getUniversity().getSubjects().stream()
                                .flatMap(s -> s.getFunctions().stream())
                                .toList()
                );
            } else if (userPermission.getSubject() != null) {
                functions.addAll(
                        userPermission.getSubject().getFunctions().stream()
                                .toList()
                );
            }
        }
        return functions;
    }
}
