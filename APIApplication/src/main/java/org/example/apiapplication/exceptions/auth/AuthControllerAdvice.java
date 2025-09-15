package org.example.apiapplication.exceptions.auth;

import org.example.apiapplication.dto.BaseExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class AuthControllerAdvice {
    @ExceptionHandler(value = TokenRefreshException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseExceptionDto handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage());
    }
}
