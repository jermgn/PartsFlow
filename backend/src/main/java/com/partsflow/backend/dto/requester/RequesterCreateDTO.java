package com.partsflow.backend.dto.requester;

import com.partsflow.backend.model.RequesterRole;

public record RequesterCreateDTO(
        String firstName,
        String lastName,
        String department,
        String site,
        RequesterRole businessRole,
        Long userId
) {}
