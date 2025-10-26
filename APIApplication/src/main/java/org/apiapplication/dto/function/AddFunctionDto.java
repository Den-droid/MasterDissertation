package org.apiapplication.dto.function;

import java.util.List;

public record AddFunctionDto(String text, int variablesCount,
                             List<Double> minValues,
                             List<Double> maxValues, int subjectId) {
}
