package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.mark.AssignmentsToMarkDto;
import org.apiapplication.dto.mark.MarkAssignmentDto;
import org.apiapplication.entities.assignment.Mark;
import org.apiapplication.entities.assignment.UserAssignment;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.repositories.MarkRepository;
import org.apiapplication.repositories.UserAssignmentRepository;
import org.apiapplication.services.interfaces.MarkService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MarkServiceImpl implements MarkService {
    MarkRepository markRepository;
    UserAssignmentRepository userAssignmentRepository;

    public MarkServiceImpl(UserAssignmentRepository userAssignmentRepository,
                           MarkRepository markRepository) {
        this.userAssignmentRepository = userAssignmentRepository;
        this.markRepository = markRepository;
    }

    @Override
    public void markAssignment(int userAssignmentId, MarkAssignmentDto markAssignmentDto) {
        Mark mark;
        if (markAssignmentDto.id() > 0) {
            mark = markRepository.findById(markAssignmentDto.id()).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.MARK, String.valueOf(markAssignmentDto.id()))
            );
            mark.setMark(markAssignmentDto.mark());
            mark.setComment(markAssignmentDto.comment());
        } else {
            mark = new Mark();
            mark.setMark(markAssignmentDto.mark());
            mark.setComment(markAssignmentDto.comment());

            UserAssignment userAssignment = userAssignmentRepository.findById(userAssignmentId).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT,
                            String.valueOf(userAssignmentId))
            );

            mark.setUserAssignment(userAssignment);
        }

        markRepository.save(mark);
    }

    @Override
    public List<AssignmentsToMarkDto> getAssignmentsToMark(int userId) {
        return List.of();
    }
}
