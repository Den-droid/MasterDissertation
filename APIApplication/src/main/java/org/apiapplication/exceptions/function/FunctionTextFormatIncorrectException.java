package org.apiapplication.exceptions.function;

public class FunctionTextFormatIncorrectException extends RuntimeException {
    public FunctionTextFormatIncorrectException() {
        super("Формат функції неправильний. Використовуйте правильні математичні конструкції!");
    }
}
