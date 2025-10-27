package org.apiapplication.services.interfaces;

import org.apiapplication.dto.common.IdDto;
import org.apiapplication.dto.function.AddFunctionDto;
import org.apiapplication.dto.function.FunctionDto;
import org.apiapplication.dto.function.UpdateFunctionDto;

import java.util.List;

public interface FunctionService {
    List<FunctionDto> get(Integer subjectId);

    IdDto add(AddFunctionDto addFunctionDto);

    void update(int id, UpdateFunctionDto updateFunctionDto);

    void delete(int id);
}
