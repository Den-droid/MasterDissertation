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

    @PutMapping("/assignments/{userAssignmentId}/putMark")
    public ResponseEntity<?> mark(@PathVariable String userAssignmentId,
                                  @RequestBody MarkDto markDto) {
        int userAssignmentIdInt;
        try {
            userAssignmentIdInt = Integer.parseInt(userAssignmentId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT, userAssignmentId);
        }
        markService.markAssignment(userAssignmentIdInt, markDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/assignments/{userAssignmentId}/marks")
    public ResponseEntity<List<MarkDto>> getMarksForAssignments(
            @PathVariable String userAssignmentId) {
        int userAssignmentIdInt;
        try {
            userAssignmentIdInt = Integer.parseInt(userAssignmentId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.USER_ASSIGNMENT, userAssignmentId);
        }
        List<MarkDto> assignmentsToMarkDto = markService.getByUserAssignmentId(userAssignmentIdInt);
        return ResponseEntity.ok(assignmentsToMarkDto);
    }
}
