package org.apiapplication.services.interfaces;

import org.apiapplication.dto.answer.AnswerDto;
import org.apiapplication.dto.assignment.*;

import java.util.List;

public interface AssignmentService {
    AssignmentDto getById(int userAssignmentId);

    List<UserAssignmentDto> get(Integer userId);

    void assign(AssignDto assignDto);

    void startContinue(int userAssignmentId);

    void finish(int userAssignmentId);

    AssignmentResponseDto answerAssignment(int userAssignmentId, AssignmentAnswerDto assignmentAnswerDto);

    List<AnswerDto> getAnswersForAssignment(int userAssignmentId);
}
