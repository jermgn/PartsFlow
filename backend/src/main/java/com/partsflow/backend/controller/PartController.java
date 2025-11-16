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
        return partRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<Part> getByReference(@PathVariable String reference) {
        return partRepository.findByReference(reference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Part incoming) {

        if (partRepository.existsByReference(incoming.getReference())) {
            return ResponseEntity.badRequest().body("Reference already exists");
        }

        Part saved = partRepository.save(incoming);
        URI location = URI.create("/api/parts/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Part update) {
        return partRepository.findById(id)
                .map(existing -> {

                    existing.setName(update.getName());
                    existing.setSupplier(update.getSupplier());
                    existing.setVersion(update.getVersion());
                    existing.setStatus(update.getStatus());

                    Part saved = partRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
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
