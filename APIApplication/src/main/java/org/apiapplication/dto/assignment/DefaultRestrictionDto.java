package org.apiapplication.dto.assignment;

import java.time.LocalDateTime;

public record DefaultRestrictionDto(int restrictionType, Integer functionId, Integer subjectId,
                                    Integer universityId, Integer attemptsRemaining,
                                    Integer minutesForAttempt, LocalDateTime deadline) {
}
