package org.apiapplication.dto.function;

public record FunctionDto(int id, String text, int variablesCount, String minValues,
                          String maxValues) {
}
