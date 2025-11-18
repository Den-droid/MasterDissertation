package org.apiapplication.controllers;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.answer.AnswerDto;
import org.apiapplication.dto.assignment.*;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.services.interfaces.AssignmentService;
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

    @GetMapping
    public ResponseEntity<List<UserAssignmentDto>> getByUserId() {
        List<UserAssignmentDto> userAssignmentsDto = assignmentService.get();
        return ResponseEntity.ok(userAssignmentsDto);
    }

    @GetMapping("/{userAssignmentId}")
    public ResponseEntity<AssignmentDto> getById(@PathVariable String userAssignmentId) {
        int userAssignmentIdInt;
        try {
            userAssignmentIdInt = Integer.parseInt(userAssignmentId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.ASSIGNMENT, userAssignmentId);
        }
        AssignmentDto assignmentDto = assignmentService.getById(userAssignmentIdInt);
        return ResponseEntity.ok(assignmentDto);
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assign(@RequestBody AssignDto assignDto) {
        assignmentService.assign(assignDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assignToGroup")
    public ResponseEntity<?> assignToGroup(@RequestBody AssignGroupDto assignGroupDto) {
        assignmentService.assign(assignGroupDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userAssignmentId}/startContinue")
    public ResponseEntity<?> startContinue(@PathVariable String userAssignmentId) {
        int userAssignmentIdInt;
        try {
            userAssignmentIdInt = Integer.parseInt(userAssignmentId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.ASSIGNMENT, userAssignmentId);
        }
        assignmentService.startContinue(userAssignmentIdInt);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userAssignmentId}/finish")
    public ResponseEntity<?> finish(@PathVariable String userAssignmentId) {
        int userAssignmentIdInt;
        try {
            userAssignmentIdInt = Integer.parseInt(userAssignmentId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.ASSIGNMENT, userAssignmentId);
        }
        assignmentService.finish(userAssignmentIdInt);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userAssignmentId}/giveAnswer")
    public ResponseEntity<AssignmentResponseDto> answer(@PathVariable String userAssignmentId,
                                                        @RequestBody AssignmentAnswerDto assignmentAnswerDto) {
        int userAssignmentIdInt;
        try {
            userAssignmentIdInt = Integer.parseInt(userAssignmentId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.ASSIGNMENT, userAssignmentId);
        }
        AssignmentResponseDto assignmentResponseDto = assignmentService.answerAssignment(userAssignmentIdInt,
                assignmentAnswerDto);
        return ResponseEntity.ok(assignmentResponseDto);
    }

    @GetMapping("/{userAssignmentId}/answers")
    public ResponseEntity<List<AnswerDto>> getAnswers(@PathVariable String userAssignmentId) {
        int userAssignmentIdInt;
        try {
            userAssignmentIdInt = Integer.parseInt(userAssignmentId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.ASSIGNMENT, userAssignmentId);
        }
        List<AnswerDto> answerDtos = assignmentService.getAnswersForAssignment(userAssignmentIdInt);
        return ResponseEntity.ok(answerDtos);
    }
}
