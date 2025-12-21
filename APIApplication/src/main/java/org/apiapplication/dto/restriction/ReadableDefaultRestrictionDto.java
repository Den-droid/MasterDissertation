package org.apiapplication.dto.restriction;

import java.time.LocalDateTime;

public record ReadableDefaultRestrictionDto(Integer id, RestrictionTypeDto restrictionType, Integer functionId,
                                            Integer subjectId, Integer universityId, Integer mazeId,
                                            Integer attemptsRemaining,
                                            Integer minutesToDo, LocalDateTime deadline) {
}
