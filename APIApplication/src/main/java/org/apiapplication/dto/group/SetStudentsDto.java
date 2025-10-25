package org.apiapplication.dto.group;

import java.util.List;

public record SetStudentsDto(int groupId, List<Integer> studentIds) {
}
