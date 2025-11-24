package com.partsflow.backend.controller;

import com.partsflow.backend.dto.approval.*;
import com.partsflow.backend.service.ApprovalStepService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@CrossOrigin(origins = "*")
public class ApprovalStepController {

    private final ApprovalStepService service;

    public ApprovalStepController(ApprovalStepService service) {
        this.service = service;
    }

    @GetMapping("/request/{requestId}")
    public List<ApprovalStepResponseDTO> getByRequest(@PathVariable Long requestId) {
        return service.getByRequest(requestId);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ApprovalStepCreateDTO dto) {
        try {
            var created = service.create(dto);
            return ResponseEntity.created(URI.create("/api/approvals/" + created.id()))
                    .body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ApprovalStepUpdateDTO dto
    ) {
        try {
            return ResponseEntity.ok(service.update(id, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
