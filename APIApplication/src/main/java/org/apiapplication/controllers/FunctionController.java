package org.apiapplication.controllers;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.function.AddFunctionDto;
import org.apiapplication.dto.function.FunctionDto;
import org.apiapplication.dto.function.UpdateFunctionDto;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.services.interfaces.FunctionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/functions")
@CrossOrigin
public class FunctionController {
    private FunctionService functionService;

    public FunctionController(FunctionService functionService) {
        this.functionService = functionService;
    }

    @GetMapping
    public ResponseEntity<List<FunctionDto>> get(@RequestParam(required = false)
                                                 Integer subjectId) {
        List<FunctionDto> functionDtos = functionService.get(subjectId);
        return ResponseEntity.ok(functionDtos);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody AddFunctionDto addFunctionDto) {
        int id = functionService.add(addFunctionDto);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{functionId}")
    public ResponseEntity<?> update(@PathVariable String functionId,
                                    @RequestBody UpdateFunctionDto updateFunctionDto) {
        int functionIdInt;
        try {
            functionIdInt = Integer.parseInt(functionId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.FUNCTION, functionId);
        }
        functionService.update(functionIdInt, updateFunctionDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{functionId}")
    public ResponseEntity<?> delete(@PathVariable String functionId) {
        int functionIdInt;
        try {
            functionIdInt = Integer.parseInt(functionId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.FUNCTION, functionId);
        }
        functionService.delete(functionIdInt);
        return ResponseEntity.ok().build();
    }
}
