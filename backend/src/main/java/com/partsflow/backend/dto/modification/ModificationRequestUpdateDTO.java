package com.partsflow.backend.dto.modification;

import com.partsflow.backend.model.RequestStatus;

public record ModificationRequestUpdateDTO(
        RequestStatus status,
        String comment,
        Long workshopId
) {}
