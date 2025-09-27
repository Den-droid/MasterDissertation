package org.apiapplication.services.interfaces;

import org.apiapplication.dto.answer.AnswerDto;
import org.apiapplication.dto.assignment.AssignmentAnswerDto;
import org.apiapplication.dto.assignment.AssignmentDto;
import org.apiapplication.dto.assignment.AssignmentResponseDto;
import org.apiapplication.dto.assignment.UserAssignmentDto;

import java.util.List;

public interface UserAssignmentService {
    AssignmentDto getById(int userAssignmentId);

    List<UserAssignmentDto> getByUser(int userId);

    void assign(int userId);

    void startContinue(int userAssignmentId);

    void finish(int userAssignmentId);

    AssignmentResponseDto answerAssignment(int userAssignmentId, AssignmentAnswerDto assignmentAnswerDto);

    List<AnswerDto> getAnswersForAssignment(int userAssignmentId);
}
