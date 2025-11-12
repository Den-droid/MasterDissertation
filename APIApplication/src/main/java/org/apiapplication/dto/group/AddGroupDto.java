package org.apiapplication.dto.group;

import java.util.List;

public record AddGroupDto(String name, List<Integer> userIds,
                          List<Integer> subjectIds) {
}
