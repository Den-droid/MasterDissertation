package org.apiapplication.exceptions.entity;

public class EntityCantBeDeletedException extends RuntimeException {
    public EntityCantBeDeletedException() {
        super("Цей ресурс не може бути видалено");
    }
}
