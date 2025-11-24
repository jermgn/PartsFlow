package com.partsflow.backend.dto.auth;

public record AuthRequestDTO(
        String username,
        String password
) {}
