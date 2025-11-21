package com.partsflow.backend.dto.workshop;

public record WorkshopCreateDTO(
        String name,
        String department,
        String code
) {}
