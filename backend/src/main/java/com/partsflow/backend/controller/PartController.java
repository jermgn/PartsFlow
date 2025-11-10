package com.partsflow.backend.controller;

import com.partsflow.backend.model.Part;
import com.partsflow.backend.repository.PartRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/parts")
@CrossOrigin(origins = "*")

public class PartController {

    private final PartRepository partRepository;

    public PartController(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @GetMapping
    public List<Part> getAll() {
        return partRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Part> getById(@PathVariable Long id) {
        Part part = partRepository.findById(id).orElse(null);
        if (part == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(part);
    }

    @PostMapping
    public ResponseEntity<Part> create(@RequestBody Part incoming) {
        if (incoming.getReference() == null || incoming.getName() == null) {
            return ResponseEntity.badRequest().build();
        }
        Part saved = partRepository.save(incoming);

        URI location = URI.create("/api/parts/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Part> update(@PathVariable Long id, @RequestBody Part updatedPart) {
        Part existing = partRepository.findById(id).orElse(null);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setReference(updatedPart.getReference());
        existing.setName(updatedPart.getName());
        existing.setSupplier(updatedPart.getSupplier());
        existing.setVersion(updatedPart.getVersion());
        existing.setStatus(updatedPart.getStatus());

        Part saved = partRepository.save(existing);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!partRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        partRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}