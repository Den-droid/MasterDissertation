package org.apiapplication.controllers;

import org.apiapplication.dto.answer.AnswerDto;
import org.apiapplication.dto.assignment.*;
import org.apiapplication.services.interfaces.UserAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin
public class AssignmentController {
    private final UserAssignmentService userAssignmentService;

    public AssignmentController(UserAssignmentService userAssignmentService) {
        this.userAssignmentService = userAssignmentService;
    }

    @GetMapping("/getByUserId")
    public ResponseEntity<List<UserAssignmentDto>> getByUserId(@RequestParam Integer userId) {
        List<UserAssignmentDto> userAssignmentsDto = userAssignmentService.getByUser(userId);
        return ResponseEntity.ok(userAssignmentsDto);
    }

    @GetMapping("/{userAssignmentId}")
    public ResponseEntity<AssignmentDto> getById(@PathVariable Integer userAssignmentId) {
        AssignmentDto assignmentDto = userAssignmentService.getById(userAssignmentId);
        return ResponseEntity.ok(assignmentDto);
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assign(@RequestBody AssignDto assignDto) {
        userAssignmentService.assign(assignDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userAssignmentId}/startContinue")
    public ResponseEntity<?> startContinue(@PathVariable Integer userAssignmentId) {
        userAssignmentService.startContinue(userAssignmentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userAssignmentId}/finish")
    public ResponseEntity<?> finish(@PathVariable Integer userAssignmentId) {
        userAssignmentService.finish(userAssignmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userAssignmentId}/answer")
    public ResponseEntity<AssignmentResponseDto> answer(@PathVariable Integer userAssignmentId,
                                                        @RequestBody AssignmentAnswerDto assignmentAnswerDto) {
        AssignmentResponseDto assignmentResponseDto = userAssignmentService.answerAssignment(userAssignmentId,
                assignmentAnswerDto);
        return ResponseEntity.ok(assignmentResponseDto);
    }

    @GetMapping("/{userAssignmentId}/answers")
    public ResponseEntity<List<AnswerDto>> getAnswers(@PathVariable Integer userAssignmentId) {
        List<AnswerDto> answerDtos = userAssignmentService.getAnswersForAssignment(userAssignmentId);
        return ResponseEntity.ok(answerDtos);
    }
}
