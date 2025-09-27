package org.apiapplication.exceptions.auth;

public class UserWithKeyNotFoundException extends RuntimeException {
    public UserWithKeyNotFoundException(String apiKey) {
        super("Користувача з ключем " + apiKey + " не існує!");
    }
}
