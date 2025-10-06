package org.apiapplication.dto.permission;

public record PermissionDto(Integer id, int userId, Integer universityId, Integer subjectId,
                            Integer functionId, Integer userAssignmentId) {
}
