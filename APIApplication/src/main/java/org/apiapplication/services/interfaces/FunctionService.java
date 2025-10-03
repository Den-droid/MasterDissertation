package org.apiapplication.services.interfaces;

import org.apiapplication.dto.function.FunctionDto;

import java.util.List;

public interface FunctionService {
    List<FunctionDto> get(Integer subjectId);
}
