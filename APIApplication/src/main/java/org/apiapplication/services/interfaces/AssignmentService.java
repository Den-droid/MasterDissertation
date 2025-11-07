package org.apiapplication.services.interfaces;

import org.apiapplication.dto.answer.AnswerDto;
import org.apiapplication.dto.assignment.*;

import java.util.List;

public interface AssignmentService {
    AssignmentDto getById(int userAssignmentId);

    List<UserAssignmentDto> get();

    void assign(AssignDto assignDto);

    void assign(AssignGroupDto assignGroupDto);

    void startContinue(int userAssignmentId);

    void finish(int userAssignmentId);

    AssignmentResponseDto answerAssignment(int userAssignmentId, AssignmentAnswerDto assignmentAnswerDto);

    List<AnswerDto> getAnswersForAssignment(int userAssignmentId);
}
