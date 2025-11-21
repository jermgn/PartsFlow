package com.partsflow.backend.dto.part;

import com.partsflow.backend.model.PartStatus;

public record PartResponseDTO(
        Long id,
        String reference,
        String name,
        String supplier,
        String version,
        PartStatus status
) {}
