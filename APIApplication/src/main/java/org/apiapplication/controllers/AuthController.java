package org.apiapplication.controllers;

import org.apiapplication.dto.auth.*;
import org.apiapplication.services.interfaces.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signIn/password")
    public ResponseEntity<?> signIn(@RequestBody SignInDto signInDto) {
        TokensDto jwtDto = authService.signIn(signInDto);
        return ResponseEntity.ok(jwtDto);
    }

    @PostMapping("/signIn/apiKey")
    public ResponseEntity<?> signIn(@RequestBody ApiKeyDto apiKeyDto) {
        TokensDto jwtDto = authService.signIn(apiKeyDto);
        return ResponseEntity.ok(jwtDto);
    }

    @PutMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        TokensDto tokensDto =
                authService.refreshToken(refreshTokenDto);
        return ResponseEntity.ok(tokensDto);
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto signUpDto) {
        authService.signUp(signUpDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/forgotPassword/tokenExists")
    public ResponseEntity<?> existsForgotPasswordToken(@RequestParam String token) {
        boolean tokenExists = authService.existsForgotPasswordToken(token);
        return ResponseEntity.ok(tokenExists);
    }

    @PostMapping("/forgotPassword/create")
    public ResponseEntity<?> createForgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        authService.createForgotPassword(forgotPasswordDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgotPassword/change")
    public ResponseEntity<?> changeForgotPassword(@RequestParam String token,
                                                  @RequestBody ChangePasswordDto changePasswordDto) {
        authService.changeForgotPassword(token, changePasswordDto);
        return ResponseEntity.ok().build();
    }
}
