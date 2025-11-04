package org.apiapplication.dto.function;

import org.apiapplication.dto.subject.SubjectDto;

import java.util.List;

public record FunctionDto(int id, String text, int variablesCount,
                          List<Double> minValues, List<Double> maxValues,
                          SubjectDto subject) {
}
