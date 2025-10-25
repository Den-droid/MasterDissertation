package org.apiapplication.services.interfaces;

import org.apiapplication.dto.group.AddGroupDto;
import org.apiapplication.dto.group.SetStudentsDto;
import org.apiapplication.dto.group.SetSubjectsDto;
import org.apiapplication.dto.group.UpdateGroupDto;

public interface GroupService {
    // add get method

    int add(AddGroupDto addGroupDto);

    void update(UpdateGroupDto updateGroupDto);

    void delete(int id);

    void setStudents(SetStudentsDto setStudentsDto);

    void setSubjects(SetSubjectsDto setSubjectsDto);
}
