package org.apiapplication.dto.group;

import java.util.List;

public record UpdateGroupDto(String name, List<Integer> userIds,
                             List<Integer> subjectIds) {
}
