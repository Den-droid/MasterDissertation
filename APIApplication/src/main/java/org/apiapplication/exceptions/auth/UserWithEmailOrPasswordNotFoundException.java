package org.apiapplication.exceptions.auth;

public class UserWithEmailOrPasswordNotFoundException extends RuntimeException {
    public UserWithEmailOrPasswordNotFoundException() {
        super("Введено неправильну електронну адресу чи пароль! Спробуйте знову");
    }
}
