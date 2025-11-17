package com.partsflow.backend.controller;

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

    private final ApprovalStepRepository approvalRepo;
    private final ModificationRequestRepository requestRepo;
    private final UserRepository userRepo;

    public ApprovalStepController(
            ApprovalStepRepository approvalRepo,
            ModificationRequestRepository requestRepo,
            UserRepository userRepo
    ) {
        this.approvalRepo = approvalRepo;
        this.requestRepo = requestRepo;
        this.userRepo = userRepo;
    }

    public record ApprovalCreateDTO(
            Long requestId,
            Long approvedByUserId,
            ApprovalStatus status,
            String comment
    ) {}

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ApprovalCreateDTO dto) {

        ModificationRequest request = requestRepo.findById(dto.requestId())
                .orElse(null);
        if (request == null)
            return ResponseEntity.badRequest().body("Request not found");

        User user = userRepo.findById(dto.approvedByUserId())
                .orElse(null);
        if (user == null)
            return ResponseEntity.badRequest().body("User approving not found");

        ApprovalStep step = ApprovalStep.builder()
                .request(request)
                .approvedBy(user)
                .status(dto.status())
                .comment(dto.comment())
                .build();

        if (dto.status() == ApprovalStatus.APPROVED) {
            request.setStatus(RequestStatus.VALIDATED);
        } else if (dto.status() == ApprovalStatus.REJECTED) {
            request.setStatus(RequestStatus.REJECTED);
        }

        requestRepo.save(request);

        ApprovalStep saved = approvalRepo.save(step);

        return ResponseEntity.created(URI.create("/api/approvals/" + saved.getId()))
                .body(saved);
    }

    @GetMapping("/request/{id}")
    public List<ApprovalStep> getApprovalsForRequest(@PathVariable Long id) {
        return approvalRepo.findByRequestId(id);
    }
}
