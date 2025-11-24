package com.partsflow.backend.dto.auth;

import com.partsflow.backend.model.UserRole;

public record AuthResponseDTO(
        String token,
        Long userId,
        String username,
        String email,
        UserRole role
) {}
