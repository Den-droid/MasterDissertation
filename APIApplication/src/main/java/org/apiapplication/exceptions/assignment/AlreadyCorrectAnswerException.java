package org.apiapplication.exceptions.assignment;

public class AlreadyCorrectAnswerException extends RuntimeException {
    public AlreadyCorrectAnswerException() {
        super("Правильна відповідь вже дана на це питання!");
    }
}
