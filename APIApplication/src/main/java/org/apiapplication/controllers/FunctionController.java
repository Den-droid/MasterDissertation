package org.apiapplication.controllers;

import org.apiapplication.dto.function.AddFunctionDto;
import org.apiapplication.dto.function.FunctionDto;
import org.apiapplication.dto.function.UpdateFunctionDto;
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
        functionService.add(addFunctionDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody UpdateFunctionDto updateFunctionDto) {
        functionService.update(updateFunctionDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam Integer functionId) {
        functionService.delete(functionId);
        return ResponseEntity.ok().build();
    }
}
