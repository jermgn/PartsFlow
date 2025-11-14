package com.partsflow.backend.controller;

import com.partsflow.backend.model.ModificationRequest;
import com.partsflow.backend.model.Part;
import com.partsflow.backend.model.PartStatus;
import com.partsflow.backend.model.RequestStatus;
import com.partsflow.backend.repository.ModificationRequestRepository;
import com.partsflow.backend.repository.PartRepository;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "*")

public class ModificationRequestController {
    
    private final ModificationRequestRepository modificationRequestRepository;
    private final PartRepository partRepository;

    public ModificationRequestController(ModificationRequestRepository modificationRequestRepository, PartRepository partRepository) {
        this.modificationRequestRepository = modificationRequestRepository;
        this.partRepository = partRepository;
    }

    @GetMapping
    public List<ModificationRequest> getAll() {
        return modificationRequestRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModificationRequest> getById(@PathVariable Long id) {
        ModificationRequest request = modificationRequestRepository.findById(id).orElse(null);

        if (request == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(request);
    }

    @PostMapping
    public ResponseEntity<ModificationRequest> create(@RequestBody ModificationRequest incoming) {
        if (incoming.getPart() == null || incoming.getRequester() == null) {
            return ResponseEntity.badRequest().build();
        }

        ModificationRequest saved = modificationRequestRepository.save(incoming);

        URI location = URI.create("/api/requests/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModificationRequest> update(@PathVariable Long id, @RequestBody ModificationRequest updateRequest) {
        ModificationRequest existing = modificationRequestRepository.findById(id).orElse(null);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setStatus(updateRequest.getStatus());
        existing.setComment(updateRequest.getComment());
        existing.setWorkshop(updateRequest.getWorkshop());

        if (updateRequest.getStatus() == RequestStatus.VALIDATED) {
            Part part = existing.getPart();
            part.setStatus(PartStatus.REPLACED);
            partRepository.save(part);
        }

        ModificationRequest saved = modificationRequestRepository.save(existing);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
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

    @GetMapping("workshop/{workshop}")
    public List<ModificationRequest> getByWorkshop(@PathVariable String workshop) {
        return modificationRequestRepository.findByWorkshop(workshop);
    }
}
