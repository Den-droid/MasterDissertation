package org.apiapplication.services.interfaces;

import org.apiapplication.dto.auth.*;
import org.example.apiapplication.dto.auth.*;

public interface AuthService {
    void signUp(SignUpDto signUpDto);

    TokensDto signIn(SignInDto signInDto);

    TokensDto refreshToken(RefreshTokenDto refreshTokenDt);

    void changeForgotPassword(String token, ChangePasswordDto changePasswordDto);

    void createForgotPassword(ForgotPasswordDto forgotPasswordDto);

    boolean existsForgotPasswordToken(String token);

}
