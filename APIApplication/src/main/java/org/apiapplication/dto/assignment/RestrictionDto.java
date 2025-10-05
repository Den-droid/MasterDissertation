package org.apiapplication.dto.assignment;

import java.time.LocalDateTime;

public record RestrictionDto(int restrictionType, Integer functionId, Integer subjectId,
                             Integer universityId, Integer userAssignmentId,
                             Integer attemptsRemaining, Integer minutesForAttempt,
                             LocalDateTime deadline
) {
}