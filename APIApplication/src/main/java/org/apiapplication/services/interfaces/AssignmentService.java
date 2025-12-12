package org.apiapplication.services.interfaces;

import org.apiapplication.dto.answer.AnswerDto;
import org.apiapplication.dto.assignment.*;

import java.util.List;

public interface AssignmentService {
    AssignmentDto getById(int userAssignmentId);

    List<UserAssignmentDto> get();

    void assignFunction(AssignFunctionDto assignFunctionDto);

    void assignFunctionToGroup(AssignGroupDto assignGroupDto);

    void assignMaze();

    void assignMazeToGroup(AssignGroupDto assignGroupDto);

    void startContinue(int userAssignmentId);

    void finish(int userAssignmentId);

    AssignmentResponseDto answer(int userAssignmentId, AssignmentAnswerDto assignmentAnswerDto);

    List<AnswerDto> getAnswersForAssignment(int userAssignmentId);
}
