package org.apiapplication.dto.assignment;

import org.apiapplication.dto.restriction.RestrictionTypeDto;

import java.time.LocalDateTime;

public record AssignmentDto(String hint, int variablesCount, AssignmentStatusDto assignmentStatus,
                            RestrictionTypeDto restrictionType,
                            int attemptsRemaining, LocalDateTime deadline,
                            LocalDateTime nextAttemptTime) {
}
