package org.apiapplication.dto.assignment;

import org.apiapplication.dto.restriction.RestrictionTypeDto;

import java.time.LocalDateTime;

public record AssignmentResponseDto(double result, boolean isWall,
                                    boolean hasCorrectAnswer,
                                    RestrictionTypeDto restrictionType,
                                    int attemptsRemaining, LocalDateTime deadline,
                                    LocalDateTime nextAttemptTime) {
}
