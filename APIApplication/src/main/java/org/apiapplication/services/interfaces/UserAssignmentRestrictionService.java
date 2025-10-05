package org.apiapplication.services.interfaces;

import org.apiapplication.dto.assignment.DefaultRestrictionDto;
import org.apiapplication.dto.assignment.RestrictionDto;
import org.apiapplication.dto.assignment.RestrictionTypeDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.entities.assignment.DefaultAssignmentRestriction;
import org.apiapplication.entities.assignment.Function;

import java.util.List;

public interface UserAssignmentRestrictionService {
    DefaultAssignmentRestriction getDefaultRestrictionForFunction(Function function);

    DefaultAssignmentRestriction getDefaultRestrictionForSubject(Subject subject);

    DefaultAssignmentRestriction getDefaultRestrictionForUniversity(University university);

    DefaultAssignmentRestriction getDefaultRestriction();

    void setDefaultRestriction(DefaultRestrictionDto restrictionDto);

    void setRestriction(RestrictionDto restrictionDto);

    List<RestrictionTypeDto> getRestrictionTypes();
}
