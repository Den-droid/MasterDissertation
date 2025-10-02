package org.apiapplication.exceptions.permission;

public class PermissionException extends RuntimeException {
    public PermissionException() {
        super("У вас немає доступу на виконання цієї дії!");
    }
}
