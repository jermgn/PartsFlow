package com.partsflow.backend.dto.modification;

public record ModificationRequestCreateDTO(
        Long partId,
        Long requesterId,
        Long workshopId,
        String comment
) {}
