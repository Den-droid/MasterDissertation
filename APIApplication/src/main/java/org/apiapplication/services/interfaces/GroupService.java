package org.apiapplication.services.interfaces;

import org.apiapplication.dto.common.IdDto;
import org.apiapplication.dto.group.*;

import java.util.List;

public interface GroupService {
    List<GroupDto> getForCurrentUser();

    GroupDto getById(int id);

    IdDto add(AddGroupDto addGroupDto);

    void update(int groupId, UpdateGroupDto updateGroupDto);

    void delete(int groupId);

    void setStudents(int groupId, SetStudentsDto setStudentsDto);

    void addStudents(int groupId, SetStudentsDto setStudentsDto);

    void removeStudents(int groupId, SetStudentsDto setStudentsDto);

    void setSubjects(int groupId, SetSubjectsDto setSubjectsDto);

    void addSubjects(int groupId, SetSubjectsDto setSubjectsDto);

    void removeSubjects(int groupId, SetSubjectsDto setSubjectsDto);
}
