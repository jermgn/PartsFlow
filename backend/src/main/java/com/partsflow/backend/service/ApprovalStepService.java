package com.partsflow.backend.service;

import com.partsflow.backend.dto.approval.*;
import com.partsflow.backend.model.*;
import com.partsflow.backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalStepService {

    private final ApprovalStepRepository approvalStepRepository;
    private final ModificationRequestRepository modificationRequestRepository;
    private final UserRepository userRepository;

    public ApprovalStepService(
            ApprovalStepRepository approvalStepRepository,
            ModificationRequestRepository modificationRequestRepository,
            UserRepository userRepository
    ) {
        this.approvalStepRepository = approvalStepRepository;
        this.modificationRequestRepository = modificationRequestRepository;
        this.userRepository = userRepository;
    }

    private ApprovalStepResponseDTO toDTO(ApprovalStep step) {
        return new ApprovalStepResponseDTO(
                step.getId(),
                step.getModificationRequest().getId(),
                step.getApprover().getId(),
                step.getApproverRole(),
                step.getStatus(),
                step.getComment(),
                step.getValidatedAt()
        );
    }

    public List<ApprovalStepResponseDTO> getByRequest(Long requestId) {
        return approvalStepRepository.findByModificationRequestId(requestId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ApprovalStepResponseDTO create(ApprovalStepCreateDTO dto) {

        var modificationRequest = modificationRequestRepository.findById(dto.modificationRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Modification Request not found"));

        var approver = userRepository.findById(dto.approverId())
                .orElseThrow(() -> new IllegalArgumentException("Approver not found"));

        ApprovalStep step = ApprovalStep.builder()
                .modificationRequest(modificationRequest)
                .approver(approver)
                .approverRole(dto.approverRole())
                .status(ApprovalStatus.PENDING)
                .build();

        ApprovalStep saved = approvalStepRepository.save(step);
        return toDTO(saved);
    }

    public ApprovalStepResponseDTO update(Long id, ApprovalStepUpdateDTO dto) {

        ApprovalStep existing = approvalStepRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Approval Step not found"));

        if (dto.status() != null) {
            existing.setStatus(dto.status());
        }

        if (dto.comment() != null) {
            existing.setComment(dto.comment());
        }

        ApprovalStep saved = approvalStepRepository.save(existing);

        return toDTO(saved);
    }
}
