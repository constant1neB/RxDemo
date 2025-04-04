package com.example.rxdemo.dto;

public record CreateUserRequest(
        String name,
        String email
) {}
