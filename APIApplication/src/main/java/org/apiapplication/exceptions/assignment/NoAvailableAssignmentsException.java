package org.apiapplication.exceptions.assignment;

public class NoAvailableAssignmentsException extends RuntimeException {
    public NoAvailableAssignmentsException() {
        super("Немає доступних завдань. Спробуйте пізніше!");
    }
}
