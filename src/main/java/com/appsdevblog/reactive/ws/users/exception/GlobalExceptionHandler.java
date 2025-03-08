package com.appsdevblog.reactive.ws.users.exception;

import com.sun.jdi.request.DuplicateRequestException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateRequestException.class)
    public Mono<ErrorResponse> handleDuplicateKeyException(DuplicateKeyException ex) {
        return Mono.just(
                ErrorResponse.builder(ex, HttpStatus.CONFLICT, ex.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public Mono<ErrorResponse> handleGeneralException(Exception ex) {
        return Mono.just(
                ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()).build());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ErrorResponse> handleBindingException(WebExchangeBindException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return Mono.just(
                ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, errorMessage).build());
    }
}
