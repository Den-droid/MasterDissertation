package org.apiapplication.services.interfaces;

import org.apiapplication.dto.restriction.*;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.entities.assignment.DefaultAssignmentRestriction;
import org.apiapplication.entities.function.Function;
import org.apiapplication.entities.maze.Maze;

import java.util.List;

public interface AssignmentRestrictionService {
    List<ReadableDefaultRestrictionDto> getDefault(Integer functionId, Integer subjectId,
                                                   Integer universityId, Integer mazeId);

    ReadableRestrictionDto getCurrent(Integer userAssignmentId);

    DefaultAssignmentRestriction getDefaultRestrictionForMaze(Maze maze);

    DefaultAssignmentRestriction getDefaultRestrictionForFunction(Function function);

    DefaultAssignmentRestriction getDefaultRestrictionForSubject(Subject subject);

    DefaultAssignmentRestriction getDefaultRestrictionForUniversity(University university);

    void setDefaultRestriction(DefaultRestrictionDto restrictionDto);

    void setRestriction(RestrictionDto restrictionDto);

    List<RestrictionTypeDto> getRestrictionTypes();
}
