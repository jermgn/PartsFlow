package com.partsflow.backend.dto.part;

import com.partsflow.backend.model.PartStatus;

public record PartUpdateDTO(
        String name,
        String supplier,
        String version,
        PartStatus status
) {}
