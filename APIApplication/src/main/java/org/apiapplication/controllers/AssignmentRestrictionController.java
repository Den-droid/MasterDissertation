package org.apiapplication.controllers;

import org.apiapplication.dto.restriction.DefaultRestrictionDto;
import org.apiapplication.dto.restriction.DeleteRestrictionDto;
import org.apiapplication.dto.restriction.RestrictionDto;
import org.apiapplication.dto.restriction.RestrictionTypeDto;
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

    @PutMapping("/setDefaultRestriction")
    public ResponseEntity<?> setDefaultRestriction(@RequestBody DefaultRestrictionDto restrictionDto) {
        userAssignmentRestrictionService.setDefaultRestriction(restrictionDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteDefaultRestriction")
    public ResponseEntity<?> deleteDefaultRestriction(@RequestBody DeleteRestrictionDto deleteRestrictionDto) {
        userAssignmentRestrictionService.deleteDefaultRestriction(deleteRestrictionDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/setRestriction")
    public ResponseEntity<?> setRestriction(@RequestBody RestrictionDto restrictionDto) {
        userAssignmentRestrictionService.setRestriction(restrictionDto);
        return ResponseEntity.ok().build();
    }
}
