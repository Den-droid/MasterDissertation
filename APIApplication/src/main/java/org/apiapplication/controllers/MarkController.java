package org.apiapplication.controllers;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.mark.MarkDto;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.services.interfaces.MarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class MarkController {
    private final MarkService markService;

    public MarkController(MarkService markService) {
        this.markService = markService;
    }

    @PutMapping("/assignments/{assignmentId}/putMark")
    public ResponseEntity<?> mark(@PathVariable String assignmentId,
                                  @RequestBody MarkDto markDto) {
        int assignmentIdInt;
        try {
            assignmentIdInt = Integer.parseInt(assignmentId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.ASSIGNMENT, assignmentId);
        }
        markService.markAssignment(assignmentIdInt, markDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/assignments/{userAssignmentId}/marks")
    public ResponseEntity<List<MarkDto>> getMarksForAssignments(
            @PathVariable int userAssignmentId) {
        List<MarkDto> assignmentsToMarkDto = markService.getByUserAssignmentId(userAssignmentId);
        return ResponseEntity.ok(assignmentsToMarkDto);
    }
}
