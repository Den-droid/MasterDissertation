package org.apiapplication.dto.answer;

public record AnswerDto(int numberOfAnswer, String answer, String result,
                        boolean isCorrect) {
}
