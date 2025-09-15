package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.dto.mark.AssignmentsToMarkDto;
import org.example.apiapplication.dto.mark.MarkAssignmentDto;
import org.example.apiapplication.entities.Assignment;
import org.example.apiapplication.entities.Mark;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.example.apiapplication.repositories.AssignmentRepository;
import org.example.apiapplication.repositories.MarkRepository;
import org.example.apiapplication.services.interfaces.MarkService;
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
