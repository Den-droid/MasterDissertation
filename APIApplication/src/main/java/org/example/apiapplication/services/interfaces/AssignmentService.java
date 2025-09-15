package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.assignment.AssignmentAnswerDto;
import org.example.apiapplication.dto.assignment.AssignmentResponseDto;
import org.example.apiapplication.dto.assignment.StartAssignmentDto;
import org.example.apiapplication.dto.assignment.UserAssignmentsDto;

import java.util.List;

public interface AssignmentService {
    List<UserAssignmentsDto> getByUser(int userId);

    boolean isAvailable(int userId);

    void assign(int userId);

    StartAssignmentDto start(int assignmentId);

    void stop(int assignmentId);

    void finish(int assignmentId);

    AssignmentResponseDto answerAssignment(int assignmentId, AssignmentAnswerDto assignmentAnswerDto);
}
