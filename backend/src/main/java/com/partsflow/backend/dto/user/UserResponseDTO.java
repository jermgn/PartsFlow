package com.partsflow.backend.dto.user;

import com.partsflow.backend.model.UserRole;

public record UserResponseDTO(
        Long id,
        String username,
        String email,
        UserRole userRole,
        Boolean enabled
) {}
