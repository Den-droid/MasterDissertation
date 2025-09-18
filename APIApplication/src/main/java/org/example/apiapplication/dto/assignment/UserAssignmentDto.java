package org.example.apiapplication.dto.assignment;

public record UserAssignmentDto(int assignmentId, String hint, int attemptsRemaining,
                                int statusId, int functionResultTypeId,
                                int mark, String comment) {
}
