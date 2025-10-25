package org.apiapplication.controllers;

import org.apiapplication.dto.university.AddUniversityDto;
import org.apiapplication.dto.university.UniversityDto;
import org.apiapplication.dto.university.UpdateUniversityDto;
import org.apiapplication.services.interfaces.UniversityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
@CrossOrigin
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

    @PostMapping
    public ResponseEntity<?> add(@RequestBody AddUniversityDto addUniversityDto) {
        int id = universityService.add(addUniversityDto);
        return ResponseEntity.ok(id);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody UpdateUniversityDto updateUniversityDto) {
        universityService.update(updateUniversityDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam int universityId) {
        universityService.delete(universityId);
        return ResponseEntity.ok().build();
    }
}
