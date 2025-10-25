package org.apiapplication.exceptions.function;

import org.apiapplication.dto.BaseExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class FunctionControllerAdvice {
    @ExceptionHandler(value = FunctionSameExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseExceptionDto handleEntityException(FunctionSameExistsException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage());
    }
}
