package org.apiapplication.dto.function;

import java.util.List;

public record UpdateFunctionDto(String text, Integer variablesCount, List<Double> minValues,
                                List<Double> maxValues, Integer subjectId) {
}
