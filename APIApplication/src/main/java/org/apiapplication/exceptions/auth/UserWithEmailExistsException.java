package org.apiapplication.exceptions.auth;

public class UserWithEmailExistsException extends RuntimeException {
    public UserWithEmailExistsException(String email) {
        super("Користувач з поштою " + email + " вже існує!");
    }
}
