package org.example.apiapplication.dto.auth;

import java.util.List;

public record UserDto (int id, List<String> roles){
}
