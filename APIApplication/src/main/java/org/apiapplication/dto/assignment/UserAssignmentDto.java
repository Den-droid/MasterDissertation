package org.apiapplication.dto.assignment;

import java.time.LocalDateTime;

public record UserAssignmentDto(int userAssignmentId, String hint, int status,
                                int functionResultType,
                                int restrictionType,
                                int attemptsRemaining, LocalDateTime deadline,
                                LocalDateTime nextAttemptTime,
                                int mark, String comment) {
}
