package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.mark.AssignmentsToMarkDto;
import org.apiapplication.dto.mark.MarkAssignmentDto;
import org.apiapplication.entities.Assignment;
import org.apiapplication.entities.Mark;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.repositories.AssignmentRepository;
import org.apiapplication.repositories.MarkRepository;
import org.apiapplication.services.interfaces.MarkService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MarkServiceImpl implements MarkService {
    MarkRepository markRepository;
    AssignmentRepository assignmentRepository;

    public MarkServiceImpl(AssignmentRepository assignmentRepository,
                           MarkRepository markRepository) {
        this.assignmentRepository = assignmentRepository;
        this.markRepository = markRepository;
    }

    @Override
    public void markAssignment(int assignmentId, MarkAssignmentDto markAssignmentDto) {
        Mark mark;
        if (markAssignmentDto.markId() > 0) {
            mark = markRepository.findById(markAssignmentDto.markId()).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.MARK, markAssignmentDto.markId())
            );
            mark.setMark(markAssignmentDto.mark());
            mark.setComment(markAssignmentDto.comment());
        } else {
            mark = new Mark();
            mark.setMark(markAssignmentDto.mark());
            mark.setComment(markAssignmentDto.comment());

            Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.ASSIGNMENT, assignmentId)
            );

            mark.setAssignment(assignment);
        }

        markRepository.save(mark);
    }

    @Override
    public List<AssignmentsToMarkDto> getAssignmentsToMark(int userId) {
        return List.of();
    }
}
