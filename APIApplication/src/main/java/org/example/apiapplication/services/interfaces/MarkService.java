package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.mark.MarkAssignmentDto;
import org.example.apiapplication.dto.mark.AssignmentsToMarkDto;

import java.util.List;

public interface MarkService {
    void markAssignment(int assignmentId, MarkAssignmentDto markAssignmentDto);
    List<AssignmentsToMarkDto> getAssignmentsForMark(int userId);
}
