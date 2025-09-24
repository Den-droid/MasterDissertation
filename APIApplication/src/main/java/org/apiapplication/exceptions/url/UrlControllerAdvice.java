package org.apiapplication.exceptions.url;

import org.apiapplication.dto.BaseExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class UrlControllerAdvice {
    @ExceptionHandler(value = UrlWithNameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseExceptionDto handleEntityException(UrlWithNameNotFoundException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());
    }
}
