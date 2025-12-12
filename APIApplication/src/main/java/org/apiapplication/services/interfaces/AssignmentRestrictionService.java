package org.apiapplication.services.interfaces;

import org.apiapplication.dto.restriction.DefaultRestrictionDto;
import org.apiapplication.dto.restriction.RestrictionDto;
import org.apiapplication.dto.restriction.RestrictionTypeDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.entities.assignment.DefaultAssignmentRestriction;
import org.apiapplication.entities.function.Function;
import org.apiapplication.entities.maze.Maze;

import java.util.List;

public interface AssignmentRestrictionService {
    List<DefaultRestrictionDto> getDefault(Integer functionId, Integer subjectId,
                                           Integer universityId, Integer mazeId);

    RestrictionDto getCurrent(Integer userAssignmentId);

    DefaultAssignmentRestriction getDefaultRestrictionForMaze(Maze maze);

    DefaultAssignmentRestriction getDefaultRestrictionForFunction(Function function);

    DefaultAssignmentRestriction getDefaultRestrictionForSubject(Subject subject);

    DefaultAssignmentRestriction getDefaultRestrictionForUniversity(University university);

    DefaultAssignmentRestriction getDefaultRestriction();

    void setDefaultRestriction(DefaultRestrictionDto restrictionDto);

    void setRestriction(RestrictionDto restrictionDto);

    List<RestrictionTypeDto> getRestrictionTypes();
}
