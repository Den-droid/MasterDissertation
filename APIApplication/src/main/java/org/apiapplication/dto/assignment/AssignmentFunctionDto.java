package org.apiapplication.dto.assignment;

import org.apiapplication.dto.function.FunctionDto;

public record AssignmentFunctionDto(FunctionDto functionDto, int userAssignmentId) {
}
