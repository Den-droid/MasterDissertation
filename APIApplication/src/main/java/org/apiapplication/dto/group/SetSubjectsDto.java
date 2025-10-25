package org.apiapplication.dto.group;

import java.util.List;

public record SetSubjectsDto(int groupId, List<Integer> subjectIds) {
}
