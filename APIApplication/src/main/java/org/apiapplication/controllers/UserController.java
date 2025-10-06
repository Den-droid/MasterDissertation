package org.apiapplication.controllers;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.auth.ApiKeyDto;
import org.apiapplication.dto.user.UserDto;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.services.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> get(@RequestParam(required = false) Integer userId) {
        List<UserDto> userDtos = userService.get(userId);
        return ResponseEntity.ok(userDtos);
    }

    @PutMapping("/{userId}/apiKey")
    public ResponseEntity<ApiKeyDto> updateApiKey(@PathVariable String userId) {
        int userIdInt;
        try {
            userIdInt = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.USER, userId);
        }
        ApiKeyDto newApiKeyDto = userService.getApiKey(userIdInt);
        return ResponseEntity.ok(newApiKeyDto);
    }
}
