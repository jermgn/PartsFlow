package com.partsflow.backend.dto.workshop;

public record WorkshopResponseDTO(
        Long id,
        String name,
        String department,
        String code
) {}
