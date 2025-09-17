package org.example.apiapplication.dto.assignment;

public record UserAssignmentDto(int statusId, int functionResultTypeId, String lastAnswer,
                                int mark, String comment, boolean lastAnswerCorrect) {
}
