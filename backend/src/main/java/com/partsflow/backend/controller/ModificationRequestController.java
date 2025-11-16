package com.partsflow.backend.controller;

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

    public record ModificationRequestCreateDTO(
            Long partId,
            Long requesterId,
            Long workshopId,
            String comment
    ) {
    }

    @GetMapping
    public List<ModificationRequest> getAll() {
        return modificationRequestRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModificationRequest> getById(@PathVariable Long id) {
        return modificationRequestRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ModificationRequestCreateDTO dto) {

        Part part = partRepository.findById(dto.partId())
                .orElse(null);

        if (part == null) {
            return ResponseEntity.badRequest().body("Part not found");
        }

        Requester requester = requesterRepository.findById(dto.requesterId())
                .orElse(null);

        if (requester == null) {
            return ResponseEntity.badRequest().body("Requester not found");
        }

        Workshop workshop = workshopRepository.findById(dto.workshopId())
                .orElse(null);

        if (workshop == null) {
            return ResponseEntity.badRequest().body("Workshop not found");
        }

        ModificationRequest request = ModificationRequest.builder()
                .part(part)
                .requester(requester)
                .workshop(workshop)
                .comment(dto.comment())
                .status(RequestStatus.PENDING)
                .build();

        ModificationRequest saved = modificationRequestRepository.save(request);

        return ResponseEntity.created(URI.create("/api/requests/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ModificationRequest update
    ) {

        ModificationRequest existing = modificationRequestRepository.findById(id)
                .orElse(null);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setComment(update.getComment());
        existing.setStatus(update.getStatus());

        if (update.getStatus() == RequestStatus.VALIDATED) {
            Part part = existing.getPart();
            part.setStatus(PartStatus.REPLACED);
            partRepository.save(part);
        }

        ModificationRequest saved = modificationRequestRepository.save(existing);
        return ResponseEntity.ok(saved);
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
    public List<ModificationRequest> getByStatus(@PathVariable RequestStatus status) {
        return modificationRequestRepository.findByStatus(status);
    }

    @GetMapping("/workshop/{id}")
    public List<ModificationRequest> getByWorkshop(@PathVariable Long id) {
        return modificationRequestRepository.findByWorkshopId(id);
    }
}
