package org.apiapplication.dto.permission;

import java.util.List;

public record UpdatePermissionDto(int userId, List<Integer> universityIds,
                                  List<Integer> subjectIds,
                                  List<Integer> functionIds, List<Integer> userAssignmentIds) {
}
