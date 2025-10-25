package org.apiapplication.controllers;

import org.apiapplication.dto.restriction.DefaultRestrictionDto;
import org.apiapplication.dto.restriction.RestrictionDto;
import org.apiapplication.dto.restriction.RestrictionTypeDto;
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

    @GetMapping("defaultRestrictions")
    public ResponseEntity<List<DefaultRestrictionDto>> getDefaultRestrictions(@RequestParam(required = false) Integer functionId,
                                                                              @RequestParam(required = false) Integer subjectId,
                                                                              @RequestParam(required = false) Integer universityId) {
        List<DefaultRestrictionDto> defaultRestrictionDtos = assignmentRestrictionService
                .get(functionId, subjectId, universityId);
        return ResponseEntity.ok(defaultRestrictionDtos);
    }

    @GetMapping("/restrictionTypes")
    public ResponseEntity<List<RestrictionTypeDto>> getRestrictionTypes() {
        List<RestrictionTypeDto> restrictionTypeDtos = assignmentRestrictionService.getRestrictionTypes();
        return ResponseEntity.ok(restrictionTypeDtos);
    }

    @PutMapping("/setDefaultRestriction")
    public ResponseEntity<?> setDefaultRestriction(@RequestBody DefaultRestrictionDto restrictionDto) {
        assignmentRestrictionService.setDefaultRestriction(restrictionDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteDefaultRestriction")
    public ResponseEntity<?> deleteDefaultRestriction(@RequestParam int defaultRestrictionId) {
        assignmentRestrictionService.deleteDefaultRestriction(defaultRestrictionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/setRestriction")
    public ResponseEntity<?> setRestriction(@RequestBody RestrictionDto restrictionDto) {
        assignmentRestrictionService.setRestriction(restrictionDto);
        return ResponseEntity.ok().build();
    }
}
