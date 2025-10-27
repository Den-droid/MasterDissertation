package org.apiapplication.dto.field;

public record FieldDto(int id, String name, String label,
                       String description, int type, boolean required, boolean multiple) {
}
