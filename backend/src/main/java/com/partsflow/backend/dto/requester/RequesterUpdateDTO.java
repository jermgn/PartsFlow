package com.partsflow.backend.dto.requester;

import com.partsflow.backend.model.RequesterRole;

public record RequesterUpdateDTO(
        String firstName,
        String lastName,
        String department,
        String site,
        RequesterRole businessRole
) {}
