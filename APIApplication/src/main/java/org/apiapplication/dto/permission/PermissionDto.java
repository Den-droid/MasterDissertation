package org.apiapplication.dto.permission;

public record PermissionDto(int userId, Integer universityId, Integer subjectId,
                            Integer functionId, Integer userAssignmentId) {
}
