package com.partsflow.backend.dto.user;

import com.partsflow.backend.model.UserRole;

public record UserCreateDTO(
        String username,
        String passwordHash,
        String email,
        UserRole userRole
) {}
