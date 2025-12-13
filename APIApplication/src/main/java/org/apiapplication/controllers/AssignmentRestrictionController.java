package org.apiapplication.controllers;

import org.apiapplication.dto.restriction.DefaultRestrictionDto;
import org.apiapplication.dto.restriction.ReadableDefaultRestrictionDto;
import org.apiapplication.dto.restriction.ReadableRestrictionDto;
import org.apiapplication.dto.restriction.RestrictionDto;
import org.apiapplication.services.interfaces.AssignmentRestrictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignmentRestrictions")
@CrossOrigin
public class AssignmentRestrictionController {
    private final AssignmentRestrictionService assignmentRestrictionService;

    public AssignmentRestrictionController(AssignmentRestrictionService
                                                   assignmentRestrictionService) {
        this.assignmentRestrictionService = assignmentRestrictionService;
    }

    @GetMapping
    public ResponseEntity<ReadableRestrictionDto> getCurrentRestriction(
            @RequestParam Integer userAssignmentId) {
        ReadableRestrictionDto restrictionDto = assignmentRestrictionService
                .getCurrent(userAssignmentId);
        return ResponseEntity.ok(restrictionDto);
    }

    @GetMapping("/defaultRestrictions")
    public ResponseEntity<List<ReadableDefaultRestrictionDto>> getDefaultRestrictions(
            @RequestParam(required = false) Integer functionId,
            @RequestParam(required = false) Integer subjectId,
            @RequestParam(required = false) Integer universityId,
            @RequestParam(required = false) Integer mazeId) {
        List<ReadableDefaultRestrictionDto> defaultRestrictionDtos = assignmentRestrictionService
                .getDefault(functionId, subjectId, universityId, mazeId);
        return ResponseEntity.ok(defaultRestrictionDtos);
    }

    @PutMapping("/setDefaultRestriction")
    public ResponseEntity<?> setDefaultRestriction(
            @RequestBody DefaultRestrictionDto defaultRestrictionDto) {
        assignmentRestrictionService.setDefaultRestriction(defaultRestrictionDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/setRestriction")
    public ResponseEntity<?> setRestriction(@RequestBody RestrictionDto restrictionDto) {
        assignmentRestrictionService.setRestriction(restrictionDto);
        return ResponseEntity.ok().build();
    }
}
