package com.partsflow.backend.controller;

import com.partsflow.backend.model.Workshop;
import com.partsflow.backend.repository.WorkshopRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/workshops")
@CrossOrigin(origins = "*")
public class WorkshopController {

    private final WorkshopRepository workshopRepository;

    public WorkshopController(WorkshopRepository workshopRepository) {
        this.workshopRepository = workshopRepository;
    }

    public record WorkshopCreateDTO(
            String name,
            String department,
            String code
    ) {}

    public record WorkshopUpdateDTO(
            String name,
            String department,
            String code
    ) {}

    @GetMapping
    public List<Workshop> getAll() {
        return workshopRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workshop> getById(@PathVariable Long id) {
        return workshopRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody WorkshopCreateDTO dto) {

        if (dto.name() == null || dto.name().isBlank()) {
            return ResponseEntity.badRequest().body("Name is required");
        }

        if (workshopRepository.existsByName(dto.name())) {
            return ResponseEntity.badRequest().body("Workshop name already exists");
        }

        Workshop workshop = Workshop.builder()
                .name(dto.name())
                .department(dto.department())
                .code(dto.code())
                .build();

        Workshop saved = workshopRepository.save(workshop);

        return ResponseEntity.created(URI.create("/api/workshops/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody WorkshopUpdateDTO dto) {
        return workshopRepository.findById(id)
                .map(existing -> {
                    existing.setName(dto.name());
                    existing.setDepartment(dto.department());
                    existing.setCode(dto.code());
                    Workshop saved = workshopRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!workshopRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        workshopRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
