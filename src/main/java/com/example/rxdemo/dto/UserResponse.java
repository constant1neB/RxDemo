package com.example.rxdemo.dto;

public record UserResponse(
        Long id,
        String name,
        String email
) {}
