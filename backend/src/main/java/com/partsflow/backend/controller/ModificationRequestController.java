package com.partsflow.backend.controller;

import com.partsflow.backend.dto.modification.*;
import com.partsflow.backend.model.*;
import com.partsflow.backend.repository.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "*")
public class ModificationRequestController {

    private final ModificationRequestRepository modificationRequestRepository;
    private final PartRepository partRepository;
    private final RequesterRepository requesterRepository;
    private final WorkshopRepository workshopRepository;

    public ModificationRequestController(
            ModificationRequestRepository modificationRequestRepository,
            PartRepository partRepository,
            RequesterRepository requesterRepository,
            WorkshopRepository workshopRepository
    ) {
        this.modificationRequestRepository = modificationRequestRepository;
        this.partRepository = partRepository;
        this.requesterRepository = requesterRepository;
        this.workshopRepository = workshopRepository;
    }

    private ModificationRequestResponseDTO toDTO(ModificationRequest req) {
        return new ModificationRequestResponseDTO(
                req.getId(),
                req.getPart().getId(),
                req.getPart().getReference(),
                req.getPart().getName(),
                req.getRequester().getId(),
                req.getRequester().getFullName(),
                req.getWorkshop().getId(),
                req.getWorkshop().getName(),
                req.getStatus(),
                req.getComment(),
                req.getCreatedAt(),
                req.getUpdatedAt()
        );
    }

    @GetMapping
    public List<ModificationRequestResponseDTO> getAll() {
        return modificationRequestRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModificationRequestResponseDTO> getById(@PathVariable Long id) {
        return modificationRequestRepository.findById(id)
                .map(req -> ResponseEntity.ok(toDTO(req)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ModificationRequestCreateDTO dto) {

        Part part = partRepository.findById(dto.partId()).orElse(null);
        if (part == null) return ResponseEntity.badRequest().body("Part not found");

        Requester requester = requesterRepository.findById(dto.requesterId()).orElse(null);
        if (requester == null) return ResponseEntity.badRequest().body("Requester not found");

        Workshop workshop = workshopRepository.findById(dto.workshopId()).orElse(null);
        if (workshop == null) return ResponseEntity.badRequest().body("Workshop not found");

        ModificationRequest req = ModificationRequest.builder()
                .part(part)
                .requester(requester)
                .workshop(workshop)
                .comment(dto.comment())
                .status(RequestStatus.PENDING)
                .build();

        ModificationRequest saved = modificationRequestRepository.save(req);

        return ResponseEntity.created(URI.create("/api/requests/" + saved.getId()))
                .body(toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody ModificationRequestUpdateDTO dto) {

        return modificationRequestRepository.findById(id)
                .map(existing -> {

                    if (dto.comment() != null) existing.setComment(dto.comment());
                    if (dto.status() != null) existing.setStatus(dto.status());
                    if (dto.workshopId() != null) {
                        workshopRepository.findById(dto.workshopId())
                                .ifPresent(existing::setWorkshop);
                    }

                    if (dto.status() == RequestStatus.VALIDATED) {
                        Part part = existing.getPart();
                        part.setStatus(PartStatus.REPLACED);
                        partRepository.save(part);
                    }

                    ModificationRequest saved = modificationRequestRepository.save(existing);
                    return ResponseEntity.ok(toDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        if (!modificationRequestRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        modificationRequestRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public List<ModificationRequestResponseDTO> getByStatus(@PathVariable RequestStatus status) {
        return modificationRequestRepository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/workshop/{id}")
    public List<ModificationRequestResponseDTO> getByWorkshop(@PathVariable Long id) {
        return modificationRequestRepository.findByWorkshopId(id)
                .stream()
                .map(this::toDTO)
                .toList();
    }
}
