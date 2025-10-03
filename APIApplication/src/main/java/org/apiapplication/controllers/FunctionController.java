package org.apiapplication.controllers;

import org.apiapplication.dto.function.FunctionDto;
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
}
