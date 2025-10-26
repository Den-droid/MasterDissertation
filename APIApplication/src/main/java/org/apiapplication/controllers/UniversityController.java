package org.apiapplication.controllers;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.university.AddUniversityDto;
import org.apiapplication.dto.university.UniversityDto;
import org.apiapplication.dto.university.UpdateUniversityDto;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
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

    @PutMapping("/{universityId}")
    public ResponseEntity<?> update(@PathVariable String universityId,
                                    @RequestBody UpdateUniversityDto updateUniversityDto) {
        int universityIdInt;
        try {
            universityIdInt = Integer.parseInt(universityId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.UNIVERSITY, universityId);
        }
        universityService.update(universityIdInt, updateUniversityDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{universityId}")
    public ResponseEntity<?> delete(@PathVariable String universityId) {
        int universityIdInt;
        try {
            universityIdInt = Integer.parseInt(universityId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.UNIVERSITY, universityId);
        }
        universityService.delete(universityIdInt);
        return ResponseEntity.ok().build();
    }
}
