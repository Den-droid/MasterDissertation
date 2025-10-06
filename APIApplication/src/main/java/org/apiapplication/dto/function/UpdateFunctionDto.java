package org.apiapplication.dto.function;

public record UpdateFunctionDto(int id, String text, int variablesCount, String minValues,
                                String maxValues, Integer subjectId) {
}
