package org.example.apiapplication.exceptions.auth;

public class UserWithEmailNotFoundException extends RuntimeException {
    public UserWithEmailNotFoundException(String email) {
        super("Користувача з електронною адресою " + email + " не існує!");
    }
}
