package com.partsflow.backend.controller;

import com.partsflow.backend.dto.workshop.WorkshopCreateDTO;
import com.partsflow.backend.dto.workshop.WorkshopResponseDTO;
import com.partsflow.backend.dto.workshop.WorkshopUpdateDTO;
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

    @GetMapping
    public List<WorkshopResponseDTO> getAll() {
        return workshopRepository.findAll().stream()
                .map(w -> new WorkshopResponseDTO(
                        w.getId(),
                        w.getName(),
                        w.getDepartment(),
                        w.getCode()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkshopResponseDTO> getById(@PathVariable Long id) {
        return workshopRepository.findById(id)
                .map(w -> ResponseEntity.ok(
                        new WorkshopResponseDTO(
                                w.getId(),
                                w.getName(),
                                w.getDepartment(),
                                w.getCode()
                        )
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody WorkshopCreateDTO dto) {

        if (dto.code() == null || dto.code().isBlank()) {
            return ResponseEntity.badRequest().body("Code is required");
        }

        if (workshopRepository.existsByCode(dto.code())) {
            return ResponseEntity.badRequest().body("Workshop code already exists");
        }

        Workshop workshop = Workshop.builder()
                .name(dto.name())
                .department(dto.department())
                .code(dto.code())
                .build();

        Workshop saved = workshopRepository.save(workshop);

        return ResponseEntity.created(URI.create("/api/workshops/" + saved.getId()))
                .body(new WorkshopResponseDTO(
                        saved.getId(),
                        saved.getName(),
                        saved.getDepartment(),
                        saved.getCode()
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody WorkshopUpdateDTO dto) {

        return workshopRepository.findById(id)
                .map(existing -> {

                    if (dto.name() != null) existing.setName(dto.name());
                    if (dto.department() != null) existing.setDepartment(dto.department());
                    if (dto.code() != null) existing.setCode(dto.code());

                    Workshop saved = workshopRepository.save(existing);

                    return ResponseEntity.ok(
                            new WorkshopResponseDTO(
                                    saved.getId(),
                                    saved.getName(),
                                    saved.getDepartment(),
                                    saved.getCode()
                            )
                    );

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
