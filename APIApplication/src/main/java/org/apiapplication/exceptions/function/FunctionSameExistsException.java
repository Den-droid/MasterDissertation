package org.apiapplication.exceptions.function;

public class FunctionSameExistsException extends RuntimeException {
    public FunctionSameExistsException() {
        super("Така ж функція вже існує!");
    }
}
