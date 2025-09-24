package org.apiapplication.dto.assignment;

import org.apiapplication.enums.AssignmentStatus;

public record AssignmentDto(String hint, int attemptRemaining,
                            int variablesCount, AssignmentStatus status) {
}
