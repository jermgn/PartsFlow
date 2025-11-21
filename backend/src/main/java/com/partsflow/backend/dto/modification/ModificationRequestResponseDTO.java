package com.partsflow.backend.dto.modification;

import java.time.LocalDateTime;

import com.partsflow.backend.model.RequestStatus;

public record ModificationRequestResponseDTO(
        Long id,
        Long partId,
        String partReference,
        String partName,
        Long requesterId,
        String requesterName,
        Long workshopId,
        String workshopName,
        RequestStatus status,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
