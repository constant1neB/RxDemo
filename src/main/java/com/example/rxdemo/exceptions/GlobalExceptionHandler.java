package com.example.rxdemo.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
        log.warn("Email uniqueness violation for request [{}}: {}", exchange.getRequest().getPath(), e.getMessage());
        return createErrorResponse(e, HttpStatus.CONFLICT, exchange);
    }

    @ExceptionHandler(DataAccessException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDataAccessException(DataAccessException e,
                                                                  ServerWebExchange exchange) {
        log.error("DataAccess violation for request [{}]: {}. Root cause {}", exchange.getRequest().getPath(), e.getMessage(), e.getMostSpecificCause().getMessage(), e);
        return createErrorResponse(e, HttpStatus.CONFLICT, exchange);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneralException(Exception e, ServerWebExchange exchange) {
        log.error("Unhandled exception occurred processing request [{}]: {}", exchange.getRequest().getPath(), e.getMessage(), e);
        return createErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, exchange);
    }

}
