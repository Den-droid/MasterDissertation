package org.apiapplication.dto.assignment;

import org.apiapplication.enums.AssignmentStatus;
import org.apiapplication.enums.FunctionResultType;

public record UserAssignmentDto(int assignmentId, String hint, int attemptsRemaining,
                                AssignmentStatus status, FunctionResultType functionResultType,
                                int mark, String comment) {
}
