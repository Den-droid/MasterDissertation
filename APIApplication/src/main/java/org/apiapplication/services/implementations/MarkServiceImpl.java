package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.mark.MarkDto;
import org.apiapplication.entities.assignment.Mark;
import org.apiapplication.entities.assignment.UserAssignment;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.MarkRepository;
import org.apiapplication.repositories.UserAssignmentRepository;
import org.apiapplication.services.interfaces.MarkService;
import org.apiapplication.services.interfaces.PermissionService;
import org.apiapplication.services.interfaces.SessionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MarkServiceImpl implements MarkService {
    private final MarkRepository markRepository;
    private final UserAssignmentRepository userAssignmentRepository;

    private final PermissionService permissionService;
    private final SessionService sessionService;

    public MarkServiceImpl(UserAssignmentRepository userAssignmentRepository,
                           MarkRepository markRepository,
                           PermissionService permissionService,
                           SessionService sessionService) {
        this.userAssignmentRepository = userAssignmentRepository;
        this.markRepository = markRepository;

        this.permissionService = permissionService;
        this.sessionService = sessionService;
    }

    @Override
    public void markAssignment(int userAssignmentId, MarkDto dto) {
        Mark mark;
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                        String.valueOf(userAssignmentId))
        );

        if (!sessionService.isUserTeacher(sessionService.getCurrentUser())
                || !permissionService.userCanAccessAssignment(sessionService.getCurrentUser(),
                userAssignment)) {
            throw new PermissionException();
        }

        if (dto.markId() != null) {
            mark = markRepository.findById(dto.markId()).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.MARK,
                            String.valueOf(dto.markId()))
            );
            mark.setMark(dto.mark());
            mark.setComment(dto.comment());
        } else {
            mark = new Mark();
            mark.setMark(dto.mark());
            mark.setComment(dto.comment());

            mark.setUserAssignment(userAssignment);
        }

        markRepository.save(mark);
    }

    @Override
    public List<MarkDto> getByUserAssignmentId(int userAssignmentId) {
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                        String.valueOf(userAssignmentId))
        );

        if (sessionService.isUserAdmin(sessionService.getCurrentUser()) ||
                !permissionService.userCanAccessAssignment(sessionService.getCurrentUser(),
                        userAssignment)) {
            throw new PermissionException();
        }

        List<MarkDto> dtos = userAssignment.getMarks().stream()
                .map(m -> new MarkDto(m.getId(), m.getMark(), m.getComment()))
                .toList();

        return dtos;
    }
}
