package org.apiapplication.dto.auth;

public record SignUpDto(String email, String password,
                        String firstName, String lastName, String role) {
}
