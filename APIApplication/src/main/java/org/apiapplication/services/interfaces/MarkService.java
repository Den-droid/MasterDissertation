package org.apiapplication.services.interfaces;

import org.apiapplication.dto.mark.MarkDto;

import java.util.List;

public interface MarkService {
    void markAssignment(int assignmentId, MarkDto markDto);

    List<MarkDto> getByUserAssignmentId(int userAssignmentId);
}
