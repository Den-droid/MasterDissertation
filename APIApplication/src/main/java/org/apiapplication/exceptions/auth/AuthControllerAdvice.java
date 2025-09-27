package org.apiapplication.exceptions.auth;

import org.apiapplication.dto.BaseExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class AuthControllerAdvice {
    @ExceptionHandler(value = UserWithEmailExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseExceptionDto handleAuthException(UserWithEmailExistsException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage());
    }

    @ExceptionHandler(value = UserWithTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseExceptionDto handleAuthException(UserWithTokenNotFoundException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());
    }

    @ExceptionHandler(value = UserWithEmailOrPasswordNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseExceptionDto handleAuthException(UserWithEmailOrPasswordNotFoundException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage());
    }

    @ExceptionHandler(value = UserWithEmailNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseExceptionDto handleAuthException(UserWithEmailNotFoundException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());
    }

    @ExceptionHandler(value = UserWithKeyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseExceptionDto handleAuthException(UserWithKeyNotFoundException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());
    }

    @ExceptionHandler(value = TokenRefreshException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseExceptionDto handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage());
    }

    @ExceptionHandler(value = UserNotApprovedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseExceptionDto handleNotApprovedException(UserNotApprovedException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage());
    }
}
