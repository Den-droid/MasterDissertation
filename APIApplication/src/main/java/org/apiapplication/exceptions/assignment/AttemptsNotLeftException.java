package org.apiapplication.exceptions.assignment;

public class AttemptsNotLeftException extends RuntimeException {
    public AttemptsNotLeftException() {
        super("Ваші спроби завершилися.");
    }
}
