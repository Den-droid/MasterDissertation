package org.apiapplication.dto.assignment;

import org.apiapplication.dto.mark.MarkDto;
import org.apiapplication.dto.restriction.RestrictionTypeDto;
import org.apiapplication.dto.user.UserDto;

import java.time.LocalDateTime;

public record UserAssignmentDto(int id, String hint, AssignmentStatusDto status,
                                RestrictionTypeDto restrictionType, AssignmentTypeDto assignmentType,
                                int attemptsRemaining, LocalDateTime deadline, int minutesToDo,
                                MarkDto mark, UserDto user) {
}
