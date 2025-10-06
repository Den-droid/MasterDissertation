package org.apiapplication.dto.restriction;

import java.time.LocalDateTime;

public record DefaultRestrictionDto(Integer id, int restrictionType, Integer functionId,
                                    Integer subjectId, Integer universityId,
                                    Integer attemptsRemaining,
                                    Integer minutesForAttempt, LocalDateTime deadline) {
}
