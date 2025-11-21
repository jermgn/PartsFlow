package com.partsflow.backend.controller;

import com.partsflow.backend.dto.part.*;
import com.partsflow.backend.model.Part;
import com.partsflow.backend.model.PartStatus;
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
    public List<PartResponseDTO> getAll() {
        return partRepository.findAll().stream()
                .map(p -> new PartResponseDTO(
                        p.getId(),
                        p.getReference(),
                        p.getName(),
                        p.getSupplier(),
                        p.getVersion(),
                        p.getStatus()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponseDTO> getById(@PathVariable Long id) {
        return partRepository.findById(id)
                .map(p -> new PartResponseDTO(
                        p.getId(),
                        p.getReference(),
                        p.getName(),
                        p.getSupplier(),
                        p.getVersion(),
                        p.getStatus()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PartCreateDTO dto) {

        if (dto.reference() == null || dto.reference().isBlank()) {
            return ResponseEntity.badRequest().body("Reference is required");
        }

        if (partRepository.existsByReference(dto.reference())) {
            return ResponseEntity.badRequest().body("Part reference already exists");
        }

        Part part = Part.builder()
                .reference(dto.reference())
                .name(dto.name())
                .supplier(dto.supplier())
                .version(dto.version())
                .status(PartStatus.ACTIVE)
                .build();

        Part saved = partRepository.save(part);

        return ResponseEntity.created(URI.create("/api/parts/" + saved.getId()))
                .body(new PartResponseDTO(
                        saved.getId(),
                        saved.getReference(),
                        saved.getName(),
                        saved.getSupplier(),
                        saved.getVersion(),
                        saved.getStatus()
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody PartUpdateDTO dto
    ) {

        return partRepository.findById(id)
                .map(existing -> {

                    if (dto.name() != null) existing.setName(dto.name());
                    if (dto.supplier() != null) existing.setSupplier(dto.supplier());
                    if (dto.version() != null) existing.setVersion(dto.version());
                    if (dto.status() != null) existing.setStatus(dto.status());

                    Part saved = partRepository.save(existing);

                    return ResponseEntity.ok(
                            new PartResponseDTO(
                                    saved.getId(),
                                    saved.getReference(),
                                    saved.getName(),
                                    saved.getSupplier(),
                                    saved.getVersion(),
                                    saved.getStatus()
                            )
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!partRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        partRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
