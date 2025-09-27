package org.apiapplication.dto.assignment;

import java.time.LocalDateTime;

public record AssignmentDto(String hint, int variablesCount, int status,
                            int restrictionType,
                            int attemptsRemaining, LocalDateTime deadline,
                            LocalDateTime nextAttemptTime) {
}
