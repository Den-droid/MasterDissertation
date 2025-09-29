package org.apiapplication.controllers;

import org.apiapplication.dto.field.FieldDto;
import org.apiapplication.services.interfaces.FieldService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fields")
@CrossOrigin
public class FieldController {
    private FieldService fieldService;

    public FieldController(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    @GetMapping
    public ResponseEntity<List<FieldDto>> getFieldsByUrl(@RequestParam Integer urlId) {
        List<FieldDto> fieldDtos = fieldService.getByUrlId(urlId);
        return ResponseEntity.ok().body(fieldDtos);
    }
}
