package org.apiapplication.dto.user;

public record UserDto(int id, String firstName, String lastName, String email, String role,
                      boolean isApproved) {
}
