package org.apiapplication.dto.assignment;

import org.apiapplication.dto.restriction.RestrictionTypeDto;

import java.time.LocalDateTime;

public record AssignmentResponseDto(String result, boolean isWall,
                                    boolean hasCorrectAnswer,
                                    RestrictionTypeDto restrictionType,
                                    int attemptsRemaining, LocalDateTime deadline,
                                    int minutesToDo) {
}
