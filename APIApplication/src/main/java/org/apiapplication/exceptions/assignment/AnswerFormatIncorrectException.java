package org.apiapplication.exceptions.assignment;

public class AnswerFormatIncorrectException extends RuntimeException {
    public AnswerFormatIncorrectException() {
        super("Формат відповіді неправильний. Введіть значення змінних у форматі: x1=2.3;x2=33;...");
    }
}
