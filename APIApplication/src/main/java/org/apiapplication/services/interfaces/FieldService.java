package org.apiapplication.services.interfaces;

import org.apiapplication.dto.field.FieldDto;

import java.util.List;

public interface FieldService {
    List<FieldDto> getByUrl(String url);
}
