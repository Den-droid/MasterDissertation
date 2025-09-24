package org.apiapplication.dto.field;

import org.apiapplication.enums.FieldType;

public record FieldDto(int id, String name, String label,
                       String description, FieldType type, boolean required) {
}
