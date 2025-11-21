package com.partsflow.backend.dto.requester;

import com.partsflow.backend.model.RequesterRole;

public record RequesterResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String department,
        String site,
        RequesterRole businessRole,
        Long userId
) {}
