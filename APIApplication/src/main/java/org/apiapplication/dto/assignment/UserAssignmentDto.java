package org.apiapplication.dto.assignment;

import org.apiapplication.dto.mark.MarkDto;
import org.apiapplication.dto.user.UserDto;

import java.time.LocalDateTime;

public record UserAssignmentDto(int id, String hint, int status,
                                int restrictionType,
                                int attemptsRemaining, LocalDateTime deadline,
                                LocalDateTime nextAttemptTime,
                                MarkDto mark, UserDto user) {
}
