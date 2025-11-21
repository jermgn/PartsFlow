package com.partsflow.backend.dto.approval;

import com.partsflow.backend.model.UserRole;

public record ApprovalStepCreateDTO(
        Long modificationRequestId,
        Long approverId,
        UserRole approverRole
) {}
