package org.apiapplication.exceptions.entity;

public class EntityWithNameNotFoundException extends RuntimeException {
    public EntityWithNameNotFoundException(String entity, String name) {
        super("Сутності " + entity + " з назвою " + name + " не існує!");
    }
}
