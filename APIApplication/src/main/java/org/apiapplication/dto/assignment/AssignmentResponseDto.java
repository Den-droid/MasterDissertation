package org.apiapplication.dto.assignment;

public record AssignmentResponseDto(double result, int attemptsRemaining, boolean hasCorrectAnswer) {
}
