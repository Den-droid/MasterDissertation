package org.apiapplication.controllers;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.answer.AnswerDto;
import org.apiapplication.dto.assignment.*;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
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
    public ResponseEntity<AssignmentDto> getById(@PathVariable String userAssignmentId) {
        int userAssignmentIdInt;
        try {
            userAssignmentIdInt = Integer.parseInt(userAssignmentId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.ASSIGNMENT, userAssignmentId);
        }
        AssignmentDto assignmentDto = userAssignmentService.getById(userAssignmentIdInt);
        return ResponseEntity.ok(assignmentDto);
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assign(@RequestBody AssignDto assignDto) {
        userAssignmentService.assign(assignDto);
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
        userAssignmentService.startContinue(userAssignmentIdInt);
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
        userAssignmentService.finish(userAssignmentIdInt);
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
        AssignmentResponseDto assignmentResponseDto = userAssignmentService.answerAssignment(userAssignmentIdInt,
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
        List<AnswerDto> answerDtos = userAssignmentService.getAnswersForAssignment(userAssignmentIdInt);
        return ResponseEntity.ok(answerDtos);
    }
}
