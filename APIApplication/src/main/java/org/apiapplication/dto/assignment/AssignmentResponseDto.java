package org.apiapplication.dto.assignment;

import java.time.LocalDateTime;

public record AssignmentResponseDto(double result, boolean hasCorrectAnswer,
                                    int restrictionType,
                                    int attemptsRemaining, LocalDateTime deadline,
                                    LocalDateTime nextAttemptTime) {
}
