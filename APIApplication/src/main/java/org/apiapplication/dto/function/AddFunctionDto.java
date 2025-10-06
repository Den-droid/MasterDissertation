package org.apiapplication.dto.function;

public record AddFunctionDto(String text, int variablesCount, String minValues,
                             String maxValues, int subjectId) {
}
