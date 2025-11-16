package org.apiapplication.dto.user;

public record CreateAdminDto(String email, String password,
                             String firstName, String lastName) {
}
