package com.partsflow.backend.dto.part;

public record PartCreateDTO(
        String reference,
        String name,
        String supplier,
        String version
) {}
