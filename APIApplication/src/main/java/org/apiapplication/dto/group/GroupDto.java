package org.apiapplication.dto.group;

import java.util.List;

public record GroupDto(int id, String name, List<GroupStudentDto> students, List<GroupSubjectDto> subjects) {
}
