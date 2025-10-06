package org.apiapplication.exceptions.entity;

public class EntityWithNameAlreadyFoundException extends RuntimeException {
    public EntityWithNameAlreadyFoundException(String entity, String name) {
        super("Сутність " + entity + " з назвою " + name + " вже існує!");
    }
}
