package org.apiapplication.controllers;

import org.apiapplication.dto.university.UniversityDto;
import org.apiapplication.services.interfaces.UniversityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
public class UniversityController {
    private UniversityService universityService;

    public UniversityController(UniversityService universityService) {
        this.universityService = universityService;
    }

    @GetMapping
    public ResponseEntity<List<UniversityDto>> getAllUniversities() {
        List<UniversityDto> universityDtoList = universityService.getAll();
        return ResponseEntity.ok().body(universityDtoList);
    }
}
