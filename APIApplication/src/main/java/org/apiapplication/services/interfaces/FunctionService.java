package org.apiapplication.services.interfaces;

import org.apiapplication.dto.function.AddFunctionDto;
import org.apiapplication.dto.function.FunctionDto;
import org.apiapplication.dto.function.UpdateFunctionDto;

import java.util.List;

public interface FunctionService {
    List<FunctionDto> get(Integer subjectId);

    void add(AddFunctionDto addFunctionDto);

    void update(UpdateFunctionDto updateFunctionDto);

    void delete(int functionId);
}
