package com.partsflow.backend.controller;

import com.partsflow.backend.dto.approval.*;
import com.partsflow.backend.model.*;
import com.partsflow.backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@CrossOrigin(origins = "*")
public class ApprovalStepController {

    private final ApprovalStepRepository approvalStepRepository;
    private final ModificationRequestRepository modificationRequestRepository;
    private final UserRepository userRepository;

    public ApprovalStepController(
            ApprovalStepRepository approvalStepRepository,
            ModificationRequestRepository modificationRequestRepository,
            UserRepository userRepository
    ) {
        this.approvalStepRepository = approvalStepRepository;
        this.modificationRequestRepository = modificationRequestRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/request/{requestId}")
    public List<ApprovalStepResponseDTO> getByRequest(@PathVariable Long requestId) {
        return approvalStepRepository.findByModificationRequestId(requestId)
                .stream()
                .map(step -> new ApprovalStepResponseDTO(
                        step.getId(),
                        step.getModificationRequest().getId(),
                        step.getApprover().getId(),
                        step.getApproverRole(),
                        step.getStatus(),
                        step.getComment(),
                        step.getValidatedAt()
                ))
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ApprovalStepCreateDTO dto) {

        var request = modificationRequestRepository.findById(dto.modificationRequestId())
                .orElse(null);

        if (request == null) {
            return ResponseEntity.badRequest().body("Modification Request not found");
        }

        var approver = userRepository.findById(dto.approverId())
                .orElse(null);

        if (approver == null) {
            return ResponseEntity.badRequest().body("Approver not found");
        }

        ApprovalStep step = ApprovalStep.builder()
                .modificationRequest(request)
                .approver(approver)
                .approverRole(dto.approverRole())
                .status(ApprovalStatus.PENDING)
                .build();

        ApprovalStep saved = approvalStepRepository.save(step);

        return ResponseEntity.created(
                URI.create("/api/approvals/" + saved.getId())
        ).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ApprovalStepUpdateDTO dto) {

        return approvalStepRepository.findById(id)
                .map(existing -> {

                    if (dto.status() != null) {
                        existing.setStatus(dto.status());
                    }

                    if (dto.comment() != null) {
                        existing.setComment(dto.comment());
                    }

                    ApprovalStep saved = approvalStepRepository.save(existing);

                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
