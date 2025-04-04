package com.example.rxdemo.exceptions;

public class EmailUniquenessException extends RuntimeException {
    public EmailUniquenessException(String message) {
        super(message);
    }
}
