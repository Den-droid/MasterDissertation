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

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getById(@PathVariable String userId) {
        int userIdInt;
        try {
            userIdInt = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.USER, userId);
        }
        UserDto userDto = userService.getById(userIdInt);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> get(@RequestParam(required = false) Integer roleId) {
        List<UserDto> userDtos = userService.get(roleId);
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

    @PutMapping("/{userId}/approve")
    public ResponseEntity<?> approve(@PathVariable String userId) {
        int userIdInt;
        try {
            userIdInt = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.USER, userId);
        }
        userService.approve(userIdInt);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/reject")
    public ResponseEntity<?> reject(@PathVariable String userId) {
        int userIdInt;
        try {
            userIdInt = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.USER, userId);
        }
        userService.reject(userIdInt);
        return ResponseEntity.ok().build();
    }
}
