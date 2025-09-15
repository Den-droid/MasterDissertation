package org.example.apiapplication.controllers;

import org.example.apiapplication.dto.assignment.AssignmentAnswerDto;
import org.example.apiapplication.dto.assignment.AssignmentDto;
import org.example.apiapplication.dto.assignment.AssignmentResponseDto;
import org.example.apiapplication.dto.assignment.UserAssignmentsDto;
import org.example.apiapplication.services.interfaces.AssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin
public class AssignmentController {
    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("/getByUserId")
    public ResponseEntity<List<UserAssignmentsDto>> getByUserId(@RequestParam Integer userId) {
        List<UserAssignmentsDto> userAssignmentsDto = assignmentService.getByUser(userId);
        return ResponseEntity.ok(userAssignmentsDto);
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentDto> getById(@PathVariable Integer assignmentId) {
        AssignmentDto assignmentDto = assignmentService.getById(assignmentId);
        return ResponseEntity.ok(assignmentDto);
    }

    @GetMapping("/isAvailable")
    public ResponseEntity<Boolean> isAvailable(@RequestParam Integer userId) {
        return ResponseEntity.ok(assignmentService.isAvailable(userId));
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assign(@RequestParam Integer userId) {
        assignmentService.assign(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{assignmentId}/start")
    public ResponseEntity<?> start(@PathVariable Integer assignmentId) {
        assignmentService.start(assignmentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{assignmentId}/stop")
    public ResponseEntity<?> stop(@PathVariable Integer assignmentId) {
        assignmentService.stop(assignmentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{assignmentId}/finish")
    public ResponseEntity<?> finish(@PathVariable Integer assignmentId) {
        assignmentService.finish(assignmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{assignmentId}/answer")
    public ResponseEntity<AssignmentResponseDto> answer(@PathVariable Integer assignmentId,
                                                        @RequestBody AssignmentAnswerDto assignmentAnswerDto) {
        AssignmentResponseDto assignmentResponseDto = assignmentService.answerAssignment(assignmentId,
                assignmentAnswerDto);
        return ResponseEntity.ok(assignmentResponseDto);
    }
}
