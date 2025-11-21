package com.partsflow.backend.dto.user;

import com.partsflow.backend.model.UserRole;

public record UserUpdateDTO(
        String username,
        String email,
        UserRole userRole,
        Boolean enabled
) {}
