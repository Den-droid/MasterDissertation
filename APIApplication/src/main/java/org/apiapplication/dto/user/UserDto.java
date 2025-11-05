package org.apiapplication.dto.user;

import org.apiapplication.dto.university.UniversityDto;

public record UserDto(int id, String firstName, String lastName, String email, String role,
                      boolean isApproved, UniversityDto university) {
}
