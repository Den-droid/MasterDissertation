package org.apiapplication.services.interfaces;

import org.apiapplication.dto.mark.MarkAssignmentDto;
import org.apiapplication.dto.mark.AssignmentsToMarkDto;

import java.util.List;

public interface MarkService {
    void markAssignment(int assignmentId, MarkAssignmentDto markAssignmentDto);
    List<AssignmentsToMarkDto> getAssignmentsToMark(int userId);
}
