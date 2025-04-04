package com.example.rxdemo.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;


@ControllerAdvice
public class GlobalExceptionHandler {

    private Mono<ResponseEntity<ErrorResponse>> createErrorResponse(
            Exception e,
            HttpStatus status,
            ServerWebExchange exchange) {
        return Mono.just(ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        Instant.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        e.getMessage(),
                        exchange.getRequest().getPath().toString()
                )));
    }

    @ExceptionHandler(EmailUniquenessException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleEmailUniquenessException(
            EmailUniquenessException e,
            ServerWebExchange exchange) {
        return createErrorResponse(e, HttpStatus.CONFLICT, exchange);
    }

    @ExceptionHandler(DataAccessException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDataAccessException(DataAccessException e,
                                                                  ServerWebExchange exchange) {
        return createErrorResponse(e, HttpStatus.CONFLICT, exchange);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneralException(Exception e, ServerWebExchange exchange) {
        return createErrorResponse(e, HttpStatus.CONFLICT, exchange);
    }

}
