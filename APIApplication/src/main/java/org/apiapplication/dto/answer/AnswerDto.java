package org.apiapplication.dto.answer;

public record AnswerDto(int numberOfAnswer, String answer, double result,
                        boolean isCorrect) {
}
