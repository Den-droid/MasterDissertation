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
import org.apiapplication.security.utils.SessionUtil;
import org.apiapplication.services.interfaces.MarkService;
import org.apiapplication.services.interfaces.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MarkServiceImpl implements MarkService {
    MarkRepository markRepository;
    UserAssignmentRepository userAssignmentRepository;

    PermissionService permissionService;

    SessionUtil sessionUtil;

    public MarkServiceImpl(UserAssignmentRepository userAssignmentRepository,
                           MarkRepository markRepository,
                           PermissionService permissionService,
                           SessionUtil sessionUtil) {
        this.userAssignmentRepository = userAssignmentRepository;
        this.markRepository = markRepository;

        this.permissionService = permissionService;

        this.sessionUtil = sessionUtil;
    }

    @Override
    public void markAssignment(int userAssignmentId, MarkDto markDto) {
        Mark mark;
        UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                        String.valueOf(userAssignmentId))
        );

        if (!permissionService.userCanAccessAssignment(sessionUtil.getUserFromSession(), userAssignment)) {
            throw new PermissionException();
        }

        if (markDto.id() > 0) {
            mark = markRepository.findById(markDto.id()).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.MARK, String.valueOf(markDto.id()))
            );
            mark.setMark(markDto.mark());
            mark.setComment(markDto.comment());
        } else {
            mark = new Mark();
            mark.setMark(markDto.mark());
            mark.setComment(markDto.comment());

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

        if (!permissionService.studentCanAccessAssignment(sessionUtil.getUserFromSession(), userAssignment)) {
            throw new PermissionException();
        }

        if (!permissionService.userCanAccessAssignment(sessionUtil.getUserFromSession(), userAssignment)) {
            throw new PermissionException();
        }

        List<MarkDto> markDtos = userAssignment.getMarks().stream()
                .map(m -> new MarkDto(m.getId(), m.getMark(), m.getComment()))
                .toList();

        return markDtos;
    }
}
