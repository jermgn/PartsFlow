package com.partsflow.backend.dto.approval;

import com.partsflow.backend.model.ApprovalStatus;

public record ApprovalStepUpdateDTO(
        ApprovalStatus status,
        String comment
) {}
