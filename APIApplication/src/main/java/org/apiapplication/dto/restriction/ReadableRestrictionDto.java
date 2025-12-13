package org.apiapplication.dto.restriction;

import java.time.LocalDateTime;

public record ReadableRestrictionDto(RestrictionTypeDto restrictionType, Integer functionId, Integer subjectId,
                                     Integer universityId, Integer userAssignmentId, Integer mazeId,
                                     Integer attemptsRemaining, Integer minutesForAttempt,
                                     LocalDateTime deadline
) {
}