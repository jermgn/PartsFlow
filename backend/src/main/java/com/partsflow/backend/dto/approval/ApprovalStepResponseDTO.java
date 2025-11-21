package com.partsflow.backend.dto.approval;

import java.time.LocalDateTime;

import com.partsflow.backend.model.ApprovalStatus;
import com.partsflow.backend.model.UserRole;

public record ApprovalStepResponseDTO(
        Long id,
        Long modificationRequestId,
        Long approverId,
        UserRole approverRole,
        ApprovalStatus status,
        String comment,
        LocalDateTime validatedAt
) {}
