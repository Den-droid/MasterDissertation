package org.apiapplication.services.interfaces;

import org.apiapplication.dto.group.AddGroupDto;
import org.apiapplication.dto.group.UpdateGroupDto;

public interface GroupService {
    int add(AddGroupDto addGroupDto);

    void update(UpdateGroupDto updateGroupDto);

    void delete(int id);

    void setStudents();

    void setSubjects();
}
