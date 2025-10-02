package org.apiapplication.exceptions.permission;

import org.apiapplication.dto.BaseExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class PermissionControllerAdvice {
    @ExceptionHandler(value = PermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseExceptionDto handleEntityException(PermissionException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage());
    }

}
