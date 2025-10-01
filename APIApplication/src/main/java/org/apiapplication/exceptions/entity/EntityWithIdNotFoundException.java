package org.apiapplication.exceptions.entity;

public class EntityWithIdNotFoundException extends RuntimeException {
    public EntityWithIdNotFoundException(String entity, String id) {
        super("Сутності " + entity + " з ідентифікатором " + id + " не існує!");
    }
}
