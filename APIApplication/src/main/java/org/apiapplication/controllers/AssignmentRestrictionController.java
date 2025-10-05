package org.apiapplication.controllers;

import org.apiapplication.dto.assignment.DefaultRestrictionDto;
import org.apiapplication.dto.assignment.RestrictionDto;
import org.apiapplication.dto.assignment.RestrictionTypeDto;
import org.apiapplication.services.interfaces.UserAssignmentRestrictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignmentRestrictions")
@CrossOrigin
public class AssignmentRestrictionController {
    private final UserAssignmentRestrictionService userAssignmentRestrictionService;

    public AssignmentRestrictionController(UserAssignmentRestrictionService
                                                   userAssignmentRestrictionService) {
        this.userAssignmentRestrictionService = userAssignmentRestrictionService;
    }

    @GetMapping("/restrictionTypes")
    public ResponseEntity<List<RestrictionTypeDto>> getRestrictionTypes() {
        List<RestrictionTypeDto> restrictionTypeDtos = userAssignmentRestrictionService.getRestrictionTypes();
        return ResponseEntity.ok(restrictionTypeDtos);
    }

    @PostMapping("/setDefaultRestriction")
    public ResponseEntity<?> setDefaultRestriction(@RequestBody DefaultRestrictionDto restrictionDto) {
        userAssignmentRestrictionService.setDefaultRestriction(restrictionDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/setRestriction")
    public ResponseEntity<?> setRestriction(@RequestBody RestrictionDto restrictionDto) {
        userAssignmentRestrictionService.setRestriction(restrictionDto);
        return ResponseEntity.ok().build();
    }
}
