package org.apiapplication.services.interfaces;

import org.apiapplication.dto.restriction.DefaultRestrictionDto;
import org.apiapplication.dto.restriction.RestrictionDto;
import org.apiapplication.dto.restriction.RestrictionTypeDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.entities.assignment.DefaultAssignmentRestriction;
import org.apiapplication.entities.assignment.Function;

import java.util.List;

public interface AssignmentRestrictionService {
    List<DefaultRestrictionDto> getDefault(Integer functionId, Integer subjectId,
                                           Integer universityId);

    RestrictionDto getCurrent(Integer userAssignmentId);

    DefaultAssignmentRestriction getDefaultRestrictionForFunction(Function function);

    DefaultAssignmentRestriction getDefaultRestrictionForSubject(Subject subject);

    DefaultAssignmentRestriction getDefaultRestrictionForUniversity(University university);

    DefaultAssignmentRestriction getDefaultRestriction();

    void setDefaultRestriction(DefaultRestrictionDto restrictionDto);

    void setRestriction(RestrictionDto restrictionDto);

    List<RestrictionTypeDto> getRestrictionTypes();
}
