package org.example.apiapplication.services.interfaces;

import org.example.apiapplication.dto.assignment.AssignmentAnswerDto;
import org.example.apiapplication.dto.assignment.AssignmentDto;
import org.example.apiapplication.dto.assignment.AssignmentResponseDto;
import org.example.apiapplication.dto.assignment.UserAssignmentDto;

import java.util.List;

public interface AssignmentService {
    AssignmentDto getById(int assignmentId);

    List<UserAssignmentDto> getByUser(int userId);

    boolean isAvailable(int userId);

    void assign(int userId);

    void start(int assignmentId);

    void stop(int assignmentId);

    void finish(int assignmentId);

    AssignmentResponseDto answerAssignment(int assignmentId, AssignmentAnswerDto assignmentAnswerDto);
}
