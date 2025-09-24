package org.apiapplication.controllers;

import org.apiapplication.dto.mark.AssignmentsToMarkDto;
import org.apiapplication.dto.mark.MarkAssignmentDto;
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

    @PutMapping("/assignments/{assignmentId}/mark")
    public ResponseEntity<?> mark(@PathVariable int assignmentId, @RequestBody MarkAssignmentDto markAssignmentDto) {
        markService.markAssignment(assignmentId, markAssignmentDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/assignments/toMark")
    public ResponseEntity<List<AssignmentsToMarkDto>> getAssignmentsToMark(@RequestParam int userId) {
        List<AssignmentsToMarkDto> assignmentsToMarkDto = markService.getAssignmentsToMark(userId);
        return ResponseEntity.ok(assignmentsToMarkDto);
    }
}
