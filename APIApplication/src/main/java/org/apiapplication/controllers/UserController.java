package org.apiapplication.controllers;

import org.apiapplication.dto.auth.ApiKeyDto;
import org.apiapplication.services.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{userId}/apiKey")
    public ResponseEntity<ApiKeyDto> updateApiKey(@PathVariable Integer userId) {
        ApiKeyDto newApiKeyDto = userService.getApiKey(userId);
        return ResponseEntity.ok(newApiKeyDto);
    }
}
